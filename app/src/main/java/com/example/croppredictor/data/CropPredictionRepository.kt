package com.example.croppredictor.data

import com.example.croppredictor.models.CropPredictionData
import com.example.croppredictor.models.CropRecommendation

class CropPredictionRepository {
    suspend fun predictCrop(data: CropPredictionData): CropRecommendation {
        // Mock prediction logic with weather consideration
        return when {
            data.nitrogen > 100 && data.humidity > 70 -> mockCrop("Rice")
            data.phosphorus > 80 && data.temperature < 30 -> mockCrop("Wheat")
            data.potassium > 70 && data.temperature > 25 -> mockCrop("Maize")
            data.pH > 7 && data.rainfall < 50 -> mockCrop("Cotton")
            else -> mockCrop("Chickpea")
        }
    }

    private fun mockCrop(cropName: String): CropRecommendation {
        return CropRecommendation(
            cropName = cropName,
            confidence = 0.85f,
            imageUrl = "file:///android_asset/crops/${cropName.lowercase()}.jpg",
            description = getDescription(cropName)
        )
    }

    private fun getDescription(cropName: String): String {
        return when (cropName) {
            "Rice" -> "Suitable for areas with high rainfall and humidity"
            "Wheat" -> "Grows well in moderate temperatures with good drainage"
            "Maize" -> "Requires well-drained soil and full sunlight"
            "Cotton" -> "Thrives in warm climate with moderate rainfall"
            else -> "Adaptable to various soil conditions"
        }
    }
} 