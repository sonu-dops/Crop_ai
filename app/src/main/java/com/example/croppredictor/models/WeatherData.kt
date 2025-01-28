package com.example.croppredictor.models

data class WeatherData(
    val temperature: Float = 0f,  // in Celsius
    val humidity: Float = 0f,     // in percentage
    val rainfall: Float = 0f      // in mm
) 