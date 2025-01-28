package com.example.croppredictor.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.croppredictor.data.db.dao.HistoryDao
import com.example.croppredictor.data.db.dao.PredictionHistoryDao
import com.example.croppredictor.data.db.entity.History
import com.example.croppredictor.data.db.entity.PredictionHistory
import com.example.croppredictor.data.db.converter.Converters

@Database(
    entities = [
        History::class,
        PredictionHistory::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun predictionHistoryDao(): PredictionHistoryDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "crop_predictor_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 