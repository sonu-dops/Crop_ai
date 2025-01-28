package com.example.croppredictor.utils

import com.example.croppredictor.models.WeatherData
import java.util.concurrent.TimeUnit

object DataCache {
    private data class CachedData<T>(
        val data: T,
        val timestamp: Long
    )

    private const val CACHE_DURATION = 30 * 60 * 1000 // 30 minutes in milliseconds
    private val weatherCache = mutableMapOf<String, CachedData<WeatherData>>()

    fun cacheWeatherData(key: String, data: WeatherData) {
        weatherCache[key] = CachedData(data, System.currentTimeMillis())
    }

    fun getWeatherData(key: String): WeatherData? {
        val cachedData = weatherCache[key] ?: return null
        
        // Check if cache is still valid
        if (System.currentTimeMillis() - cachedData.timestamp > CACHE_DURATION) {
            weatherCache.remove(key)
            return null
        }
        
        return cachedData.data
    }

    fun clearCache() {
        weatherCache.clear()
    }
} 