package com.example.croppredictor.ml

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FarmingIssueDetector(private val context: Context) {
    private val farmingKeywords = setOf(
        "plant", "crop", "leaf", "soil", "pest", "disease",
        "insect", "fungus", "weed", "nutrient deficiency",
        "irrigation", "drought", "flood", "harvest",
        "seedling", "root", "stem", "fruit", "vegetable"
    )

    private val labeler = ImageLabeling.getClient(
        ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.7f)
            .build()
    )

    suspend fun analyzeImage(bitmap: Bitmap): FarmingAnalysisResult {
        return suspendCancellableCoroutine { continuation ->
            val image = InputImage.fromBitmap(bitmap, 0)
            
            labeler.process(image)
                .addOnSuccessListener { labels ->
                    val farmingLabels = labels.filter { label ->
                        farmingKeywords.any { keyword ->
                            label.text.lowercase().contains(keyword)
                        }
                    }

                    if (farmingLabels.isEmpty()) {
                        continuation.resume(
                            FarmingAnalysisResult(
                                isFarmingRelated = false,
                                message = "No farming-related issues detected in this image."
                            )
                        )
                        return@addOnSuccessListener
                    }

                    // Analyze the detected farming issues
                    val issues = farmingLabels.map { label ->
                        FarmingIssue(
                            type = detectIssueType(label.text),
                            confidence = label.confidence,
                            description = getIssueDescription(label.text)
                        )
                    }

                    continuation.resume(
                        FarmingAnalysisResult(
                            isFarmingRelated = true,
                            issues = issues,
                            message = "Detected ${issues.size} farming-related issues."
                        )
                    )
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    private fun detectIssueType(label: String): IssueType {
        return when {
            label.contains(Regex("pest|insect|bug", RegexOption.IGNORE_CASE)) -> 
                IssueType.PEST
            label.contains(Regex("disease|fungus|blight|rot", RegexOption.IGNORE_CASE)) -> 
                IssueType.DISEASE
            label.contains(Regex("nutrient|deficiency", RegexOption.IGNORE_CASE)) -> 
                IssueType.NUTRIENT_DEFICIENCY
            label.contains(Regex("weed", RegexOption.IGNORE_CASE)) -> 
                IssueType.WEED
            label.contains(Regex("water|irrigation|drought", RegexOption.IGNORE_CASE)) -> 
                IssueType.WATER_ISSUE
            else -> IssueType.OTHER
        }
    }

    private fun getIssueDescription(label: String): String {
        return when (detectIssueType(label)) {
            IssueType.PEST -> "Detected potential pest infestation. Consider using appropriate pest control measures."
            IssueType.DISEASE -> "Plant disease detected. Isolate affected plants and consider fungicide treatment."
            IssueType.NUTRIENT_DEFICIENCY -> "Signs of nutrient deficiency observed. Soil testing recommended."
            IssueType.WEED -> "Weed growth detected. Consider manual removal or targeted herbicide application."
            IssueType.WATER_ISSUE -> "Possible irrigation issues detected. Check water management practices."
            IssueType.OTHER -> "General farming issue detected. Further inspection recommended."
        }
    }
}

data class FarmingAnalysisResult(
    val isFarmingRelated: Boolean,
    val issues: List<FarmingIssue> = emptyList(),
    val message: String
)

data class FarmingIssue(
    val type: IssueType,
    val confidence: Float,
    val description: String
)

enum class IssueType {
    PEST,
    DISEASE,
    NUTRIENT_DEFICIENCY,
    WEED,
    WATER_ISSUE,
    OTHER
} 