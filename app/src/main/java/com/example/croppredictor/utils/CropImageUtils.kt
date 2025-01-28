package com.example.croppredictor.utils

object CropImageUtils {
    fun getCropImageUrl(cropName: String): String {
        return when (cropName.lowercase()) {
            "rice" -> "https://i.ibb.co/VvC0hhV/rice.jpg"
            "wheat" -> "https://i.ibb.co/B2kw7yV/wheat.jpg"
            "maize" -> "https://i.ibb.co/PW2XkKw/maize.jpg"
            "chickpea" -> "https://i.ibb.co/YX77XM3/chickpea.jpg"
            "kidneybeans" -> "https://i.ibb.co/GCQXkBx/kidneybeans.jpg"
            "pigeonpeas" -> "https://i.ibb.co/C6m91Xz/pigeonpeas.jpg"
            "mothbeans" -> "https://i.ibb.co/VqGVXyP/mothbeans.jpg"
            "mungbean" -> "https://i.ibb.co/YBbhL1h/mungbean.jpg"
            "blackgram" -> "https://i.ibb.co/Kj01L6Y/blackgram.jpg"
            "lentil" -> "https://i.ibb.co/xSPyqNm/lentil.jpg"
            "pomegranate" -> "https://i.ibb.co/VDkKxVX/pomegranate.jpg"
            "banana" -> "https://i.ibb.co/wYBbPRg/banana.jpg"
            "mango" -> "https://i.ibb.co/Lkm1yCs/mango.jpg"
            "grapes" -> "https://i.ibb.co/PxGRZTk/grapes.jpg"
            "watermelon" -> "https://i.ibb.co/0MKXh4k/watermelon.jpg"
            "muskmelon" -> "https://i.ibb.co/VwL8RQV/muskmelon.jpg"
            "apple" -> "https://i.ibb.co/Kz8jgFy/apple.jpg"
            "orange" -> "https://i.ibb.co/C1BvYvJ/orange.jpg"
            "papaya" -> "https://i.ibb.co/0QKyPx9/papaya.jpg"
            "coconut" -> "https://i.ibb.co/RYBg8GV/coconut.jpg"
            "cotton" -> "https://i.ibb.co/0MKm0M5/cotton.jpg"
            "jute" -> "https://i.ibb.co/VvRFQRP/jute.jpg"
            "coffee" -> "https://i.ibb.co/Kz3LQWV/coffee.jpg"
            "sugarcane" -> "https://i.ibb.co/YhXKv8P/sugarcane.jpg"
            "tea" -> "https://i.ibb.co/VwPJZLh/tea.jpg"
            "turmeric" -> "https://i.ibb.co/0YL8Mvw/turmeric.jpg"
            "ginger" -> "https://i.ibb.co/RYmKv8P/ginger.jpg"
            "mustard" -> "https://i.ibb.co/VwPJZLh/mustard.jpg"
            "groundnut" -> "https://i.ibb.co/YhXKv8P/groundnut.jpg"
            "soybean" -> "https://i.ibb.co/0YL8Mvw/soybean.jpg"
            "sunflower" -> "https://i.ibb.co/RYmKv8P/sunflower.jpg"
            "pearl millet" -> "https://i.ibb.co/VwPJZLh/pearl-millet.jpg"
            "green peas" -> "https://i.ibb.co/YhXKv8P/green-peas.jpg"
            "spinach" -> "https://i.ibb.co/0YL8Mvw/spinach.jpg"
            "carrot" -> "https://i.ibb.co/RYmKv8P/carrot.jpg"
            else -> "https://i.ibb.co/YhXKv8P/default-crop.jpg"
        }
    }
} 