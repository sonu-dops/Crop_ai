package com.example.croppredictor.base

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LanguageHelper {
    fun updateBaseContextLocale(context: Context): Context {
        val locale = getStoredLocale(context)
        Locale.setDefault(locale)
        return updateResourcesLocale(context, locale)
    }

    fun setLocale(context: Context) {
        val locale = getStoredLocale(context)
        updateResourcesLocale(context, locale)
    }

    private fun getStoredLocale(context: Context): Locale {
        val languageCode = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getString("language", "en") ?: "en"
        return Locale(languageCode)
    }

    private fun updateResourcesLocale(context: Context, locale: Locale): Context {
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }
} 