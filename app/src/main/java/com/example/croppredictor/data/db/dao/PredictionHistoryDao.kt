package com.example.croppredictor.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.croppredictor.data.db.entity.PredictionHistory

@Dao
interface PredictionHistoryDao {
    @Query("SELECT * FROM prediction_history ORDER BY timestamp DESC")
    fun getAllPredictions(): LiveData<List<PredictionHistory>>

    @Insert
    suspend fun insert(prediction: PredictionHistory)
} 