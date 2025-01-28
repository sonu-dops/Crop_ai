package com.example.croppredictor.ui.prediction

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.croppredictor.data.CropPredictionRepository
import com.example.croppredictor.data.WeatherRepository
import com.example.croppredictor.data.db.AppDatabase
import com.example.croppredictor.data.db.entity.History
import com.example.croppredictor.data.db.entity.PredictionHistory
import com.example.croppredictor.models.CropPredictionData
import com.example.croppredictor.models.CropRecommendation
import com.example.croppredictor.models.WeatherData
import com.example.croppredictor.CropPredictorApp
import com.example.croppredictor.ml.CropPredictor
import com.example.croppredictor.utils.CacheManager
import kotlinx.coroutines.launch

class PredictionViewModel(application: Application) : AndroidViewModel(application) {
    private val weatherRepository = WeatherRepository()
    private val cropPredictionRepository = CropPredictionRepository()
    private val historyDao = AppDatabase.getInstance(application).historyDao()
    private val cropPredictor = CropPredictor(CropPredictorApp.instance)
    private val database = AppDatabase.getInstance(application)

    private val _weatherData = MutableLiveData<WeatherData>()
    val weatherData: LiveData<WeatherData> = _weatherData

    private val _predictions = MutableLiveData<List<CropRecommendation>>()
    val predictions: LiveData<List<CropRecommendation>> = _predictions

    private val _prediction = MutableLiveData<CropRecommendation>()
    val prediction: LiveData<CropRecommendation> = _prediction

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _weatherDataFetched = MutableLiveData<Boolean>()
    val weatherDataFetched: LiveData<Boolean> = _weatherDataFetched

    private val _isWeatherLoading = MutableLiveData<Boolean>()
    val isWeatherLoading: LiveData<Boolean> = _isWeatherLoading

    fun fetchWeatherData(latitude: Double, longitude: Double) {
        if (_isWeatherLoading.value == true) return  // Prevent double fetching
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _isWeatherLoading.value = true
                
                // Try to get cached weather data first
                val cacheKey = "$latitude,$longitude"
                CacheManager.getWeatherData(cacheKey)?.let { cachedData ->
                    _weatherData.value = cachedData
                    _weatherDataFetched.value = true
                    _isWeatherLoading.value = false
                    _isLoading.value = false
                    return@launch
                }

                // If no cache, fetch from network
                val weather = weatherRepository.getWeatherData(latitude, longitude)
                _weatherData.value = weather
                _weatherDataFetched.value = true
                
                // Cache the new weather data
                CacheManager.cacheWeatherData(cacheKey, weather)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to fetch weather data"
            } finally {
                _isWeatherLoading.value = false
                _isLoading.value = false
            }
        }
    }

    fun predict(data: CropPredictionData) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val recommendation = cropPredictor.predictCrop(data)
                _prediction.value = recommendation
                
                // Save to history
                try {
                    saveToHistory(data, recommendation)
                } catch (e: Exception) {
                    _error.value = "Error saving to history: ${e.message}"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to predict crop"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun saveToHistory(data: CropPredictionData, recommendation: CropRecommendation) {
        // Save to old history format
        val history = History(
            cropName = recommendation.cropName,
            confidence = recommendation.confidence,
            nitrogen = data.nitrogen,
            phosphorus = data.phosphorus,
            potassium = data.potassium,
            pH = data.pH,
            temperature = data.temperature,
            humidity = data.humidity,
            rainfall = data.rainfall
        )
        historyDao.insert(history)

        // Also save to new prediction history format
        val predictionHistory = PredictionHistory(
            cropName = recommendation.cropName,
            confidence = recommendation.confidence,
            nitrogen = data.nitrogen,
            phosphorus = data.phosphorus,
            potassium = data.potassium,
            ph = data.pH,
            temperature = data.temperature,
            humidity = data.humidity,
            rainfall = data.rainfall,
            imageUrl = recommendation.imageUrl
        )
        database.predictionHistoryDao().insert(predictionHistory)
    }

    fun savePrediction(prediction: PredictionHistory) {
        viewModelScope.launch {
            database.predictionHistoryDao().insert(prediction)
        }
    }

    override fun onCleared() {
        super.onCleared()
        cropPredictor.close()
    }
} 