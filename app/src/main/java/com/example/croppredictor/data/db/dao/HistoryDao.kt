package com.example.croppredictor.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.croppredictor.data.db.entity.History
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<History>>

    @Insert
    suspend fun insert(history: History)

    @Query("DELETE FROM history")
    suspend fun deleteAll()
} 