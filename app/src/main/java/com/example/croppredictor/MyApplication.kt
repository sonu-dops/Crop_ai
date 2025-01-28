package com.example.croppredictor

import android.app.Application
import com.example.croppredictor.utils.DataCache

class MyApplication : Application() {
    companion object {
        lateinit var instance: MyApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun onLowMemory() {
        super.onLowMemory()
        DataCache.clearCache()
    }
} 