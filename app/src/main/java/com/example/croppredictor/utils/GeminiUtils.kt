package com.example.croppredictor.utils

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment

fun Fragment.openGeminiAI() {
    try {
        // First try to open Gemini app using the correct package name
        val geminiIntent = requireContext().packageManager
            .getLaunchIntentForPackage("com.google.android.apps.bard")
        if (geminiIntent != null) {
            startActivity(geminiIntent)
            return
        }

        // If app not installed, try to open in browser
        val webIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://gemini.google.com")
        }
        startActivity(webIntent)
    } catch (e: Exception) {
        // If both attempts fail, open Play Store
        try {
            val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=com.google.android.apps.bard")
                setPackage("com.android.vending")
            }
            startActivity(playStoreIntent)
        } catch (e: Exception) {
            // If Play Store app is not available, open web Play Store
            val webPlayStoreIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.bard")
            }
            startActivity(webPlayStoreIntent)
        }
    }
} 