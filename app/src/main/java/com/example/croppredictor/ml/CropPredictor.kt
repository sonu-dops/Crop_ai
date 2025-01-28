package com.example.croppredictor.ml

import android.content.Context
import com.example.croppredictor.models.CropPredictionData
import com.example.croppredictor.models.CropRecommendation
import com.example.croppredictor.utils.CropImageUtils

class CropPredictor(private val context: Context) {
    
    fun predictCrop(data: CropPredictionData): CropRecommendation {
        // Get all suitable crops with their confidence scores
        val suitableCrops = mutableListOf<CropRecommendation>()

        // Check each crop's conditions and add to list if suitable
        // Rice conditions
        if (data.rainfall > 100 && data.humidity > 80 && data.temperature in 20.0..35.0 
            && data.pH in 5.5..6.5) {
            suitableCrops.add(mockCrop("Rice", calculateConfidence(0.95f, data)))
        }

        // Cotton conditions
        if (data.temperature in 25.0..35.0 && data.rainfall in 60.0..110.0 
            && data.pH > 5.5 && data.nitrogen < 140) {
            suitableCrops.add(mockCrop("Cotton", calculateConfidence(0.92f, data)))
        }

        // Sugarcane conditions
        if (data.temperature in 20.0..35.0 && data.rainfall > 150 
            && data.pH in 6.0..7.5 && data.nitrogen > 120) {
            suitableCrops.add(mockCrop("Sugarcane", calculateConfidence(0.94f, data)))
        }

        // Maize conditions
        if (data.temperature in 20.0..30.0 && data.rainfall in 50.0..80.0 
            && data.nitrogen in 80.0..120.0) {
            suitableCrops.add(mockCrop("Maize", calculateConfidence(0.91f, data)))
        }

        // Wheat conditions
        if (data.temperature in 15.0..25.0 && data.rainfall in 40.0..100.0 
            && data.pH in 6.0..7.0) {
            suitableCrops.add(mockCrop("Wheat", calculateConfidence(0.93f, data)))
        }

        // Chickpea/Gram conditions
        if (data.temperature in 15.0..25.0 && data.rainfall < 60 
            && data.pH in 6.0..8.0) {
            suitableCrops.add(mockCrop("Chickpea", calculateConfidence(0.89f, data)))
        }

        // Mustard conditions
        if (data.temperature in 15.0..25.0 && data.rainfall in 30.0..60.0) {
            suitableCrops.add(mockCrop("Mustard", calculateConfidence(0.88f, data)))
        }

        // Black Gram conditions
        if (data.temperature > 25 && data.rainfall in 60.0..100.0 
            && data.pH in 6.0..7.0) {
            suitableCrops.add(mockCrop("Black Gram", calculateConfidence(0.87f, data)))
        }

        // Green Gram/Mung Bean conditions
        if (data.temperature > 28 && data.rainfall in 50.0..90.0 
            && data.nitrogen < 100) {
            suitableCrops.add(mockCrop("Mung Bean", calculateConfidence(0.86f, data)))
        }

        // Pigeon Pea/Tur Dal conditions
        if (data.temperature in 25.0..35.0 && data.rainfall in 60.0..150.0) {
            suitableCrops.add(mockCrop("Pigeon Pea", calculateConfidence(0.88f, data)))
        }

        // Jute conditions
        if (data.temperature in 25.0..35.0 && data.rainfall > 150 
            && data.humidity > 70) {
            suitableCrops.add(mockCrop("Jute", calculateConfidence(0.90f, data)))
        }

        // Groundnut conditions
        if (data.temperature in 25.0..30.0 && data.pH in 6.0..6.5 
            && data.rainfall in 50.0..120.0) {
            suitableCrops.add(mockCrop("Groundnut", calculateConfidence(0.89f, data)))
        }

        // Soybean conditions
        if (data.temperature in 20.0..30.0 && data.pH in 6.0..6.5 
            && data.rainfall in 60.0..100.0) {
            suitableCrops.add(mockCrop("Soybean", calculateConfidence(0.87f, data)))
        }

        // Potato conditions
        if (data.temperature in 15.0..20.0 && data.nitrogen > 80 
            && data.pH in 5.0..6.5) {
            suitableCrops.add(mockCrop("Potato", calculateConfidence(0.91f, data)))
        }

        // Tomato conditions
        if (data.temperature in 20.0..27.0 && data.pH in 6.0..6.8 
            && data.nitrogen in 60.0..100.0) {
            suitableCrops.add(mockCrop("Tomato", calculateConfidence(0.88f, data)))
        }

        // Onion conditions
        if (data.temperature in 18.0..25.0 && data.pH in 6.0..7.0) {
            suitableCrops.add(mockCrop("Onion", calculateConfidence(0.87f, data)))
        }

        // Mango conditions
        if (data.temperature in 24.0..35.0 && data.rainfall in 60.0..150.0 
            && data.pH in 5.5..7.5) {
            suitableCrops.add(mockCrop("Mango", calculateConfidence(0.92f, data)))
        }

        // Banana conditions
        if (data.temperature in 25.0..35.0 && data.humidity > 75 
            && data.rainfall > 120) {
            suitableCrops.add(mockCrop("Banana", calculateConfidence(0.93f, data)))
        }

        // Citrus/Orange conditions
        if (data.temperature in 20.0..35.0 && data.pH in 5.5..7.0 
            && data.rainfall in 80.0..150.0) {
            suitableCrops.add(mockCrop("Orange", calculateConfidence(0.89f, data)))
        }

        // Turmeric conditions
        if (data.temperature in 20.0..30.0 && data.rainfall in 150.0..200.0) {
            suitableCrops.add(mockCrop("Turmeric", calculateConfidence(0.86f, data)))
        }

        // Chilli conditions
        if (data.temperature in 20.0..30.0 && data.pH in 6.0..7.0) {
            suitableCrops.add(mockCrop("Chilli", calculateConfidence(0.85f, data)))
        }

        // Jowar/Sorghum conditions
        if (data.temperature in 25.0..35.0 && data.rainfall in 40.0..100.0 
            && data.pH in 5.5..8.5) {
            suitableCrops.add(mockCrop("Sorghum", calculateConfidence(0.88f, data)))
        }

        // Bajra/Pearl Millet conditions
        if (data.temperature > 30 && data.rainfall in 30.0..100.0 
            && data.pH in 6.0..8.5) {
            suitableCrops.add(mockCrop("Bajra", calculateConfidence(0.87f, data)))
        }

        // Ragi/Finger Millet conditions
        if (data.temperature in 20.0..30.0 && data.rainfall in 50.0..100.0) {
            suitableCrops.add(mockCrop("Ragi", calculateConfidence(0.86f, data)))
        }

        // Red Gram conditions
        if (data.temperature in 25.0..35.0 && data.pH in 6.0..7.0) {
            suitableCrops.add(mockCrop("Red Gram", calculateConfidence(0.85f, data)))
        }

        // Horse Gram conditions
        if (data.temperature in 20.0..30.0 && data.rainfall < 70) {
            suitableCrops.add(mockCrop("Horse Gram", calculateConfidence(0.83f, data)))
        }

        // Sesame/Til conditions
        if (data.temperature in 25.0..35.0 && data.rainfall in 50.0..100.0 
            && data.pH in 5.5..8.0) {
            suitableCrops.add(mockCrop("Sesame", calculateConfidence(0.86f, data)))
        }

        // Sunflower conditions
        if (data.temperature in 20.0..30.0 && data.pH in 6.0..8.0) {
            suitableCrops.add(mockCrop("Sunflower", calculateConfidence(0.85f, data)))
        }

        // Castor conditions
        if (data.temperature > 20 && data.rainfall in 50.0..100.0) {
            suitableCrops.add(mockCrop("Castor", calculateConfidence(0.84f, data)))
        }

        // Brinjal/Eggplant conditions
        if (data.temperature in 20.0..30.0 && data.nitrogen > 60) {
            suitableCrops.add(mockCrop("Brinjal", calculateConfidence(0.87f, data)))
        }

        // Cauliflower conditions
        if (data.temperature in 15.0..25.0 && data.nitrogen > 80) {
            suitableCrops.add(mockCrop("Cauliflower", calculateConfidence(0.86f, data)))
        }

        // Cabbage conditions
        if (data.temperature in 15.0..25.0 && data.nitrogen > 70) {
            suitableCrops.add(mockCrop("Cabbage", calculateConfidence(0.85f, data)))
        }

        // Ginger conditions
        if (data.temperature in 20.0..30.0 && data.rainfall > 150 
            && data.humidity > 70) {
            suitableCrops.add(mockCrop("Ginger", calculateConfidence(0.89f, data)))
        }

        // Cardamom conditions
        if (data.temperature in 18.0..28.0 && data.rainfall > 200 
            && data.humidity > 75) {
            suitableCrops.add(mockCrop("Cardamom", calculateConfidence(0.91f, data)))
        }

        // Black Pepper conditions
        if (data.temperature in 20.0..30.0 && data.rainfall > 200 
            && data.humidity > 80) {
            suitableCrops.add(mockCrop("Black Pepper", calculateConfidence(0.90f, data)))
        }

        // Guava conditions
        if (data.temperature in 20.0..30.0 && data.pH in 6.0..7.5) {
            suitableCrops.add(mockCrop("Guava", calculateConfidence(0.88f, data)))
        }

        // Litchi conditions
        if (data.temperature in 20.0..30.0 && data.humidity > 65) {
            suitableCrops.add(mockCrop("Litchi", calculateConfidence(0.87f, data)))
        }

        // Pineapple conditions
        if (data.temperature in 22.0..32.0 && data.rainfall > 150 
            && data.pH in 4.5..6.5) {
            suitableCrops.add(mockCrop("Pineapple", calculateConfidence(0.89f, data)))
        }

        // Coffee conditions
        if (data.temperature in 15.0..25.0 && data.rainfall > 150 
            && data.humidity > 70 && data.pH in 6.0..6.5) {
            suitableCrops.add(mockCrop("Coffee", calculateConfidence(0.92f, data)))
        }

        // Rubber conditions
        if (data.temperature in 20.0..30.0 && data.rainfall > 200 
            && data.humidity > 80) {
            suitableCrops.add(mockCrop("Rubber", calculateConfidence(0.91f, data)))
        }

        // Coconut conditions
        if (data.temperature in 20.0..35.0 && data.rainfall > 130 
            && data.humidity > 60) {
            suitableCrops.add(mockCrop("Coconut", calculateConfidence(0.90f, data)))
        }

        // Amaranth/Rajgira conditions
        if (data.temperature in 25.0..35.0 && data.rainfall in 60.0..100.0) {
            suitableCrops.add(mockCrop("Amaranth", calculateConfidence(0.85f, data)))
        }

        // Finger Millet/Nachni conditions
        if (data.temperature in 20.0..30.0 && data.rainfall in 50.0..100.0) {
            suitableCrops.add(mockCrop("Finger Millet", calculateConfidence(0.86f, data)))
        }

        // Water Chestnut conditions
        if (data.temperature in 20.0..30.0 && data.rainfall > 100) {
            suitableCrops.add(mockCrop("Water Chestnut", calculateConfidence(0.84f, data)))
        }

        // Moth Bean conditions
        if (data.temperature > 30 && data.rainfall < 70 && data.pH in 6.0..8.0) {
            suitableCrops.add(mockCrop("Moth Bean", calculateConfidence(0.85f, data)))
        }

        // Cowpea conditions
        if (data.temperature in 25.0..35.0 && data.rainfall in 50.0..100.0) {
            suitableCrops.add(mockCrop("Cowpea", calculateConfidence(0.84f, data)))
        }

        // Bitter Gourd conditions
        if (data.temperature in 25.0..35.0 && data.humidity > 60) {
            suitableCrops.add(mockCrop("Bitter Gourd", calculateConfidence(0.86f, data)))
        }

        // Bottle Gourd conditions
        if (data.temperature in 25.0..35.0 && data.nitrogen > 50) {
            suitableCrops.add(mockCrop("Bottle Gourd", calculateConfidence(0.85f, data)))
        }

        // Ridge Gourd conditions
        if (data.temperature in 25.0..35.0 && data.humidity > 65) {
            suitableCrops.add(mockCrop("Ridge Gourd", calculateConfidence(0.84f, data)))
        }

        // Fenugreek/Methi conditions
        if (data.temperature in 15.0..25.0 && data.nitrogen > 60) {
            suitableCrops.add(mockCrop("Fenugreek", calculateConfidence(0.87f, data)))
        }

        // Amaranth Leaves/Chaulai conditions
        if (data.temperature in 20.0..30.0 && data.nitrogen > 70) {
            suitableCrops.add(mockCrop("Amaranth Leaves", calculateConfidence(0.86f, data)))
        }

        // Coriander conditions
        if (data.temperature in 15.0..25.0 && data.pH in 6.0..7.0) {
            suitableCrops.add(mockCrop("Coriander", calculateConfidence(0.88f, data)))
        }

        // Cumin/Jeera conditions
        if (data.temperature in 20.0..30.0 && data.rainfall < 60) {
            suitableCrops.add(mockCrop("Cumin", calculateConfidence(0.89f, data)))
        }

        // Fennel/Saunf conditions
        if (data.temperature in 15.0..25.0 && data.pH in 6.0..7.0) {
            suitableCrops.add(mockCrop("Fennel", calculateConfidence(0.87f, data)))
        }

        // Custard Apple conditions
        if (data.temperature in 25.0..35.0 && data.rainfall in 70.0..100.0) {
            suitableCrops.add(mockCrop("Custard Apple", calculateConfidence(0.88f, data)))
        }

        // Jackfruit conditions
        if (data.temperature in 25.0..35.0 && data.rainfall > 150) {
            suitableCrops.add(mockCrop("Jackfruit", calculateConfidence(0.89f, data)))
        }

        // Sapota/Chikoo conditions
        if (data.temperature in 20.0..35.0 && data.rainfall > 100) {
            suitableCrops.add(mockCrop("Sapota", calculateConfidence(0.87f, data)))
        }

        // Lemongrass conditions
        if (data.temperature in 25.0..35.0 && data.rainfall > 200) {
            suitableCrops.add(mockCrop("Lemongrass", calculateConfidence(0.86f, data)))
        }

        // Aloe Vera conditions
        if (data.temperature > 25 && data.rainfall < 80) {
            suitableCrops.add(mockCrop("Aloe Vera", calculateConfidence(0.88f, data)))
        }

        // Stevia conditions
        if (data.temperature in 20.0..30.0 && data.pH in 6.0..7.5) {
            suitableCrops.add(mockCrop("Stevia", calculateConfidence(0.85f, data)))
        }

        // Marigold conditions
        if (data.temperature in 20.0..30.0 && data.nitrogen > 50) {
            suitableCrops.add(mockCrop("Marigold", calculateConfidence(0.86f, data)))
        }

        // Rose conditions
        if (data.temperature in 15.0..25.0 && data.pH in 6.0..7.0) {
            suitableCrops.add(mockCrop("Rose", calculateConfidence(0.87f, data)))
        }

        // Jasmine conditions
        if (data.temperature in 20.0..30.0 && data.humidity > 60) {
            suitableCrops.add(mockCrop("Jasmine", calculateConfidence(0.88f, data)))
        }

        // If no specific crops match, add general recommendations
        if (suitableCrops.isEmpty()) {
            when {
                data.nitrogen > 100 && data.temperature < 25 -> 
                    suitableCrops.add(mockCrop("Spinach", 0.82f))
                data.phosphorus > 80 && data.pH in 6.0..7.0 -> 
                    suitableCrops.add(mockCrop("Carrot", 0.81f))
                data.rainfall > 200 && data.humidity > 70 -> 
                    suitableCrops.add(mockCrop("Tea", 0.83f))
                data.rainfall < 50 && data.temperature > 25 -> 
                    suitableCrops.add(mockCrop("Pearl Millet", 0.80f))
                else -> suitableCrops.add(mockCrop("Green Peas", 0.75f))
            }
        }

        // Sort by confidence and return the best match
        return suitableCrops.maxByOrNull { it.confidence } 
            ?: mockCrop("Green Peas", 0.75f) // Default fallback
    }

    private fun calculateConfidence(baseConfidence: Float, data: CropPredictionData): Float {
        var confidence = baseConfidence
        
        // Adjust confidence based on how optimal the conditions are
        if (data.pH < 5.0 || data.pH > 8.0) confidence -= 0.1f
        if (data.nitrogen < 40 || data.nitrogen > 200) confidence -= 0.1f
        if (data.phosphorus < 30 || data.phosphorus > 150) confidence -= 0.1f
        if (data.potassium < 30 || data.potassium > 150) confidence -= 0.1f
        if (data.rainfall < 50 || data.rainfall > 300) confidence -= 0.1f
        if (data.temperature < 15 || data.temperature > 35) confidence -= 0.1f
        if (data.humidity < 30 || data.humidity > 90) confidence -= 0.1f

        return confidence.coerceIn(0f, 1f)
    }

    private fun mockCrop(cropName: String, confidence: Float): CropRecommendation {
        return CropRecommendation(
            cropName = cropName,
            confidence = confidence,
            imageUrl = CropImageUtils.getCropImageUrl(cropName),
            description = getCropDescription(cropName)
        )
    }

    private fun getCropDescription(cropName: String): String {
        val defaultDescription = "A suitable crop for your conditions."
        return try {
            context.resources.getString(
                context.resources.getIdentifier(
                    "crop_description_${cropName.lowercase().replace(" ", "_")}",
                    "string",
                    context.packageName
                )
            )
        } catch (e: Exception) {
            defaultDescription
        }
    }

    fun close() {
        // Will be implemented when TFLite model is added
    }
} 