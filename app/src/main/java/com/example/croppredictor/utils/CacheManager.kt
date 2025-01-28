package com.example.croppredictor.utils

import android.content.Context
import android.util.LruCache
import com.example.croppredictor.models.WeatherData
import java.io.File

object CacheManager {
    private const val CACHE_SIZE = 4 * 1024 * 1024 // 4MB
    private const val WEATHER_CACHE_EXPIRY = 30 * 60 * 1000 // 30 minutes

    private var memoryCache: LruCache<String, Any>? = null
    
    fun initialize(context: Context) {
        // Initialize memory cache
        memoryCache = LruCache(CACHE_SIZE)
        
        // Clear old disk cache files
        clearOldCache(context)
    }

    fun clearCache() {
        memoryCache?.evictAll()
    }

    private fun clearOldCache(context: Context) {
        val cacheDir = File(context.cacheDir, "weather_cache")
        if (cacheDir.exists()) {
            val currentTime = System.currentTimeMillis()
            cacheDir.listFiles()?.forEach { file ->
                if (currentTime - file.lastModified() > WEATHER_CACHE_EXPIRY) {
                    file.delete()
                }
            }
        }
    }

    fun cacheWeatherData(key: String, data: WeatherData) {
        memoryCache?.put(key, data)
    }

    fun getWeatherData(key: String): WeatherData? {
        return memoryCache?.get(key) as? WeatherData
    }
} 