package com.example.croppredictor.models

data class CropRecommendation(
    val cropName: String,
    val confidence: Float,
    val imageUrl: String,
    val description: String,
    val suitabilityScore: Float = confidence // Add this for sorting
) 