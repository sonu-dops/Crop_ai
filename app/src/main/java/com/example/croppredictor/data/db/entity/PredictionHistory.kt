package com.example.croppredictor.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "prediction_history")
data class PredictionHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cropName: String,
    val confidence: Float,
    val nitrogen: Float,
    val phosphorus: Float,
    val potassium: Float,
    val ph: Float,
    val temperature: Float,
    val humidity: Float,
    val rainfall: Float,
    val timestamp: Date = Date(),
    val imageUrl: String? = null
) 