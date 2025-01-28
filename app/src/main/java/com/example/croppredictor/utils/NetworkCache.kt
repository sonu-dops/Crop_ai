package com.example.croppredictor.utils

import android.content.Context
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit

object NetworkCache {
    private const val CACHE_SIZE = 50L * 1024L * 1024L // 50 MB
    private const val CACHE_VALID_TIME = 30 // 30 minutes
    
    fun createOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(createCache(context))
            .addInterceptor(createCacheInterceptor())
            .addInterceptor(createOfflineInterceptor(context))
            .addInterceptor(createLoggingInterceptor())
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    private fun createCache(context: Context): Cache {
        return Cache(
            directory = File(context.cacheDir, "http_cache"),
            maxSize = CACHE_SIZE
        )
    }

    private fun createCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            val response = chain.proceed(chain.request())

            val cacheControl = CacheControl.Builder()
                .maxAge(CACHE_VALID_TIME, TimeUnit.MINUTES)
                .build()

            response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .header("Cache-Control", cacheControl.toString())
                .build()
        }
    }

    private fun createOfflineInterceptor(context: Context): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()

            if (!NetworkUtils.isNetworkAvailable(context)) {
                val cacheControl = CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build()

                request = request.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .cacheControl(cacheControl)
                    .build()
            }

            chain.proceed(request)
        }
    }

    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
    }
} 