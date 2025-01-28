package com.example.croppredictor.ui.analysis

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.croppredictor.R
import com.example.croppredictor.databinding.FragmentFarmingAnalysisBinding
import com.example.croppredictor.ml.FarmingIssueDetector
import com.example.croppredictor.ml.FarmingAnalysisResult
import com.example.croppredictor.ml.FarmingIssue
import com.example.croppredictor.ml.IssueType
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FarmingAnalysisFragment : Fragment() {
    private var _binding: FragmentFarmingAnalysisBinding? = null
    private val binding get() = _binding!!
    private lateinit var farmingIssueDetector: FarmingIssueDetector
    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        farmingIssueDetector = FarmingIssueDetector(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFarmingAnalysisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnCamera.setOnClickListener {
            checkCameraPermission()
        }

        binding.btnGallery.setOnClickListener {
            openGallery()
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showPermissionRationaleDialog()
            }
            else -> {
                requestCameraPermission()
            }
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestCameraPermission() {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun showPermissionRationaleDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Camera Permission Required")
            .setMessage("Camera access is needed to capture crop images for analysis")
            .setPositiveButton("Grant") { _, _ -> requestCameraPermission() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openCamera() {
        val photoFile = createImageFile()
        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            photoFile
        )

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }

        try {
            cameraLauncher.launch(takePictureIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to open camera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            photoUri?.let { uri ->
                analyzeImage(uri)
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                analyzeImage(uri)
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().getExternalFilesDir(null)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun analyzeImage(uri: Uri) {
        binding.imageView.setImageURI(uri)
        binding.progressBar.visibility = View.VISIBLE
        binding.tvResult.text = ""
        binding.tvIssues.visibility = View.GONE

        try {
            val bitmap = when {
                uri.scheme == "content" -> {
                    MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                }
                else -> {
                    BitmapFactory.decodeFile(uri.path)
                }
            }

            lifecycleScope.launch {
                try {
                    val result = farmingIssueDetector.analyzeImage(bitmap)
                    showAnalysisResult(result)
                } catch (e: Exception) {
                    Toast.makeText(context, "Analysis failed: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    binding.progressBar.visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error loading image: ${e.message}", Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showAnalysisResult(result: FarmingAnalysisResult) {
        binding.tvResult.text = result.message
        
        if (result.isFarmingRelated && result.issues.isNotEmpty()) {
            val issuesText = result.issues.joinToString(separator = "\n\n") { issue ->
                buildString {
                    append("Type: ${issue.type.name}\n")
                    append("Confidence: ${(issue.confidence * 100).toInt()}%\n")
                    append("Description: ${issue.description}")
                }
            }
            binding.tvIssues.text = issuesText
            binding.tvIssues.visibility = View.VISIBLE
        } else {
            binding.tvIssues.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 