package com.example.croppredictor.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.croppredictor.data.db.AppDatabase
import com.example.croppredictor.data.db.entity.PredictionHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val _history = MutableLiveData<List<PredictionHistory>>()
    val history: LiveData<List<PredictionHistory>> = _history

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            try {
                val predictions = withContext(Dispatchers.IO) {
                    database.predictionHistoryDao().getAllPredictions()
                }
                // Sort predictions by timestamp in descending order
                _history.value = predictions.sortedByDescending { prediction -> prediction.timestamp }
            } catch (e: Exception) {
                _history.value = emptyList()
            }
        }
    }
} 