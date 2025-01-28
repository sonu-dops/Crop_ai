package com.example.croppredictor

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import java.util.Locale
import com.example.croppredictor.utils.DataCache
import com.example.croppredictor.utils.CacheManager
import android.content.ComponentCallbacks2
import androidx.appcompat.app.AppCompatDelegate

class CropPredictorApp : Application() {
    companion object {
        lateinit var instance: CropPredictorApp
            private set
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(updateBaseContextLocale(base))
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        CacheManager.initialize(this)
        
        // Apply saved theme settings
        applySavedTheme()
    }

    private fun applySavedTheme() {
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val savedThemeMode = prefs.getInt(
            "theme_mode", 
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )
        AppCompatDelegate.setDefaultNightMode(savedThemeMode)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        CacheManager.clearCache()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
            CacheManager.clearCache()
        }
    }

    private fun updateBaseContextLocale(context: Context): Context {
        val languageCode = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getString("language", "en") ?: "en"
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }
} 