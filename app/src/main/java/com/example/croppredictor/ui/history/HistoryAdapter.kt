package com.example.croppredictor.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.croppredictor.R
import com.example.croppredictor.data.db.entity.PredictionHistory
import com.example.croppredictor.databinding.ItemPredictionHistoryBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter : ListAdapter<PredictionHistory, HistoryAdapter.ViewHolder>(HistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPredictionHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemPredictionHistoryBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: PredictionHistory) {
            binding.apply {
                tvCropName.text = item.cropName
                
                // Format date safely
                val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                tvDate.text = try {
                    dateFormat.format(item.timestamp)
                } catch (e: Exception) {
                    "Date not available"
                }
                
                // Format conditions safely
                tvConditions.text = try {
                    buildString {
                        append("N: ${item.nitrogen}, ")
                        append("P: ${item.phosphorus}, ")
                        append("K: ${item.potassium}\n")
                        append("pH: ${item.ph}, ")
                        append("Temp: ${item.temperature}°C\n")
                        append("Humidity: ${item.humidity}%, ")
                        append("Rainfall: ${item.rainfall}mm")
                    }
                } catch (e: Exception) {
                    "Data not available"
                }
                
                // Set confidence safely
                chipConfidence.text = try {
                    "Confidence: ${(item.confidence * 100).toInt()}%"
                } catch (e: Exception) {
                    "Confidence not available"
                }
                
                // Load image safely
                Glide.with(itemView.context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.ic_crop)
                    .error(R.drawable.ic_crop)
                    .transform(RoundedCorners(8))
                    .into(ivCrop)
            }
        }
    }

    private class HistoryDiffCallback : DiffUtil.ItemCallback<PredictionHistory>() {
        override fun areItemsTheSame(oldItem: PredictionHistory, newItem: PredictionHistory) = 
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: PredictionHistory, newItem: PredictionHistory) = 
            oldItem == newItem
    }
} 