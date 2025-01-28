package com.example.croppredictor.data

import com.example.croppredictor.CropPredictorApp
import com.example.croppredictor.models.WeatherData
import com.example.croppredictor.utils.DataCache
import com.example.croppredictor.utils.NetworkCache
import com.example.croppredictor.utils.NetworkUtils
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface WeatherApiService {
    @GET("forecast.json")
    suspend fun getWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("days") days: Int = 1,
        @Query("aqi") airQuality: String = "no"
    ): WeatherApiResponse
}

class WeatherRepository {
    private val apiKey = "f48a839a9c2842dca7a91142252701"
    
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(NetworkCache.createOkHttpClient(CropPredictorApp.instance))
            .build()
    }

    private val weatherService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }

    suspend fun getWeatherData(latitude: Double, longitude: Double): WeatherData {
        return withContext(Dispatchers.IO) {
            try {
                val cacheKey = "$latitude,$longitude"
                
                // Try to get from memory cache first
                DataCache.getWeatherData(cacheKey)?.let { 
                    return@withContext it 
                }

                // If not in cache or cache expired, fetch from network
                val response = weatherService.getWeather(
                    apiKey = apiKey,
                    location = "$latitude,$longitude"
                )

                val weatherData = WeatherData(
                    temperature = response.current.temp_c,
                    humidity = response.current.humidity.toFloat(),
                    rainfall = response.current.precip_mm + 
                             (response.forecast.forecastday.firstOrNull()?.day?.totalprecip_mm ?: 0f)
                )

                // Cache the new data
                DataCache.cacheWeatherData(cacheKey, weatherData)
                
                weatherData
            } catch (e: Exception) {
                if (!NetworkUtils.isNetworkAvailable(CropPredictorApp.instance)) {
                    throw Exception("No internet connection. Please check your network settings.")
                }
                throw Exception("Failed to fetch weather data: ${e.message}")
            }
        }
    }
}

data class WeatherApiResponse(
    val current: Current,
    val forecast: Forecast
)

data class Current(
    @SerializedName("temp_c")
    val temp_c: Float,
    
    @SerializedName("humidity")
    val humidity: Int,
    
    @SerializedName("precip_mm")
    val precip_mm: Float
)

data class Forecast(
    @SerializedName("forecastday")
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val day: Day
)

data class Day(
    @SerializedName("totalprecip_mm")
    val totalprecip_mm: Float
) 