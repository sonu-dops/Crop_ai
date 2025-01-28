package com.example.croppredictor.ui.settings

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("settings", Context.MODE_PRIVATE)
    
    private val _settingsChanged = MutableLiveData<Boolean>()
    val settingsChanged: LiveData<Boolean> = _settingsChanged

    fun getThemeMode(): Int {
        return prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    fun saveThemeMode(mode: Int) {
        prefs.edit()
            .putInt("theme_mode", mode)
            .apply()
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun getLanguage(): String {
        return prefs.getString("language", "en") ?: "en"
    }

    fun saveLanguage(languageCode: String) {
        prefs.edit().putString("language", languageCode).apply()
    }

    fun isDataSavingEnabled(): Boolean {
        return prefs.getBoolean("data_saving_mode", false)
    }

    fun setDataSavingEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("data_saving_mode", enabled).apply()
    }

    fun getWeatherUpdateInterval(): Int {
        return prefs.getInt("weather_update_interval", 30) // Default 30 minutes
    }

    fun setWeatherUpdateInterval(minutes: Int) {
        prefs.edit().putInt("weather_update_interval", minutes).apply()
    }

    fun isNotificationsEnabled(): Boolean {
        return prefs.getBoolean("notifications_enabled", true)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
    }

    fun getUnitSystem(): String {
        return prefs.getString("unit_system", "metric") ?: "metric"
    }

    fun setUnitSystem(system: String) {
        prefs.edit().putString("unit_system", system).apply()
    }

    fun isOfflineModeEnabled(): Boolean {
        return false // TODO: Implement actual offline mode check
    }

    fun saveSettings(isOfflineEnabled: Boolean) {
        // TODO: Implement settings save
    }
} 