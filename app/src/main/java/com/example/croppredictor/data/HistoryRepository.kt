package com.example.croppredictor.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.croppredictor.models.CropRecommendation

class HistoryRepository {
    private val predictions = mutableListOf<CropRecommendation>()
    private val _predictionsLiveData = MutableLiveData<List<CropRecommendation>>()
    val predictionsLiveData: LiveData<List<CropRecommendation>> = _predictionsLiveData

    init {
        _predictionsLiveData.value = emptyList()
    }

    fun savePrediction(prediction: CropRecommendation) {
        predictions.add(0, prediction) // Add to start of list
        _predictionsLiveData.value = predictions.toList()
    }

    fun deletePrediction(prediction: CropRecommendation) {
        predictions.remove(prediction)
        _predictionsLiveData.value = predictions.toList()
    }

    fun getAllPredictions(): List<CropRecommendation> {
        return predictions.toList()
    }

    fun clearHistory() {
        predictions.clear()
        _predictionsLiveData.value = emptyList()
    }
} 