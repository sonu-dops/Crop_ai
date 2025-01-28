package com.example.croppredictor.models

data class CropPredictionData(
    val nitrogen: Float,
    val phosphorus: Float,
    val potassium: Float,
    val pH: Float,
    val temperature: Float,
    val humidity: Float,
    val rainfall: Float
) 