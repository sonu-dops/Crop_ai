package com.example.croppredictor.ui.prediction

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.croppredictor.R
import com.example.croppredictor.databinding.FragmentPredictionBinding
import com.example.croppredictor.databinding.DialogPredictionResultBinding
import com.example.croppredictor.models.CropPredictionData
import com.example.croppredictor.models.CropRecommendation
import com.example.croppredictor.models.WeatherData
import com.google.android.gms.location.*
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import java.util.Locale
import com.example.croppredictor.utils.openGeminiAI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import java.text.SimpleDateFormat
import java.util.Date
import android.util.Log
import android.location.Location

class PredictionFragment : Fragment() {
    private var _binding: FragmentPredictionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PredictionViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: String = ""
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var isLocationUpdatesStarted = false
    private lateinit var geocoder: Geocoder

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted
                getLastLocation()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted
                getLastLocation()
            }
            else -> {
                // No location access granted
                Toast.makeText(
                    context,
                    "Location permission is required for weather data",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true
        setupLocationRequest()
    }

    private fun setupLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY
            interval = 5000
            fastestInterval = 3000
            maxWaitTime = 8000
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    stopLocationUpdates()
                    fetchAddressAndWeather(location)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPredictionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLocationClient()
        setupClickListeners()
        observeViewModel()
        geocoder = Geocoder(requireContext(), Locale.getDefault())
    }

    private fun setupLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private fun setupClickListeners() {
        with(binding) {
            btnFetchWeather.setOnClickListener {
                if (viewModel.isWeatherLoading.value != true) {
                    checkLocationPermission()
                }
            }

            btnPredict.setOnClickListener {
                if (validateInputs()) {
                    predictCrop()
                }
            }

            btnFarmingAnalysis.setOnClickListener {
                safeNavigateToAnalysis()
            }
        }
    }

    private fun safeNavigateToAnalysis() {
        if (!isAdded) {
            return  // Don't navigate if fragment is not added
        }

        try {
            val navController = findNavController()
            
            // Verify current destination
            if (navController.currentDestination?.id != R.id.predictionFragment) {
                Log.e("Navigation", "Not in PredictionFragment")
                return
            }

            // Verify the action exists
            val action = navController.currentDestination?.getAction(R.id.action_predictionFragment_to_farmingAnalysisFragment)
            if (action == null) {
                Log.e("Navigation", "Navigation action not found")
                Toast.makeText(requireContext(), "Navigation action not found", Toast.LENGTH_SHORT).show()
                return
            }

            // Perform the navigation
            navController.navigate(R.id.action_predictionFragment_to_farmingAnalysisFragment)
        } catch (e: Exception) {
            Log.e("Navigation", "Navigation failed", e)
            // Show a more detailed error message
            val errorMessage = when (e) {
                is IllegalArgumentException -> "Invalid navigation request"
                is IllegalStateException -> "Navigation not properly initialized"
                else -> "Unable to open analysis page. Please try again."
            }
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        viewModel.weatherData.observe(viewLifecycleOwner) { weather ->
            weather?.let { updateWeatherUI(it) }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.btnPredict.isEnabled = !isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                showError(error)
            }
        }

        viewModel.prediction.observe(viewLifecycleOwner) { prediction ->
            prediction?.let {
                showPredictionResult(it)
            }
        }

        viewModel.isWeatherLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnFetchWeather.apply {
                isEnabled = !isLoading
                text = if (isLoading) {
                    getString(R.string.fetching_weather)
                } else {
                    getString(R.string.fetch_weather_data)
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        // Add your validation logic here
        return true
    }

    private fun predictCrop() {
        try {
            val data = CropPredictionData(
                nitrogen = binding.etNitrogen.text.toString().toFloat(),
                phosphorus = binding.etPhosphorus.text.toString().toFloat(),
                potassium = binding.etPotassium.text.toString().toFloat(),
                pH = binding.etPh.text.toString().toFloat(),
                temperature = binding.etTemperature.text.toString().toFloat(),
                humidity = binding.etHumidity.text.toString().toFloat(),
                rainfall = binding.etRainfall.text.toString().toFloat()
            )
            viewModel.predict(data)
        } catch (e: Exception) {
            showError("Please fill all fields with valid numbers")
        }
    }

    private fun showError(message: String) {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getLastLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showLocationPermissionRationaleDialog()
            }
            else -> {
                requestLocationPermission()
            }
        }
    }

    private fun requestLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun showLocationPermissionRationaleDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(R.string.permission_required)
            .setMessage(R.string.location_permission_explanation)
            .setPositiveButton(R.string.grant) { _, _ ->
                requestLocationPermission()
            }
            .setNegativeButton(R.string.deny) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (!isLocationEnabled()) {
            showEnableLocationDialog()
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    fetchAddressAndWeather(location)
                } else {
                    // If location is null, start location updates
                    startLocationUpdates()
                }
            }
            .addOnFailureListener { e ->
                showError("Failed to get location: ${e.message}")
            }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (!isLocationUpdatesStarted) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
            isLocationUpdatesStarted = true
        }
    }

    private fun stopLocationUpdates() {
        if (isLocationUpdatesStarted) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            isLocationUpdatesStarted = false
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(LocationManager::class.java)
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun showEnableLocationDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(R.string.location_services_disabled)
            .setMessage(R.string.enable_location_message)
            .setPositiveButton(R.string.open_settings) { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun fetchAddressAndWeather(location: Location) {
        try {
            updateLocationDisplay(location.latitude, location.longitude)
            viewModel.fetchWeatherData(location.latitude, location.longitude)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Error fetching weather data: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateLocationUI(location: Location) {
        // Fetch weather for this location
        fetchAddressAndWeather(location)
    }

    private fun updateWeatherUI(weatherData: WeatherData) {
        binding.apply {
            etTemperature.setText(weatherData.temperature.toString())
            etHumidity.setText(weatherData.humidity.toString())
            etRainfall.setText(weatherData.rainfall.toString())
            
            // Update location if available
            if (currentLocation.isNotEmpty()) {
                tvLocation.text = currentLocation
            }
        }
    }

    private fun showPredictionResult(recommendation: CropRecommendation) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        
        val dialogBinding = DialogPredictionResultBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        with(dialogBinding) {
            tvCropName.text = recommendation.cropName
            tvConfidence.text = getString(
                R.string.confidence_format,
                (recommendation.confidence * 100).toInt()
            )
            tvDescription.text = recommendation.description

            Glide.with(requireContext())
                .load(recommendation.imageUrl)
                .placeholder(R.drawable.ic_crop)
                .error(R.drawable.ic_crop)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(ivCrop)

            btnClose.setOnClickListener {
                dialog.dismiss()
            }

            // Add buttons to see alternative crops
            btnNextCrop.setOnClickListener {
                // Show next recommendation
            }
            btnPrevCrop.setOnClickListener {
                // Show previous recommendation
            }

            btnYoutube.setOnClickListener {
                openYoutubeSearch(recommendation.cropName)
            }
        }

        dialog.show()
    }

    private fun openYoutubeSearch(cropName: String) {
        val searchQuery = "how to grow $cropName farming guide"
        try {
            // Try to open in YouTube app first
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("youtube://results?search_query=${Uri.encode(searchQuery)}")
            }
            startActivity(intent)
        } catch (e: Exception) {
            // If YouTube app is not installed, open in browser
            val webIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(searchQuery)}")
            }
            startActivity(webIntent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save any necessary state
        outState.putString("currentLocation", currentLocation)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            currentLocation = it.getString("currentLocation", "")
            if (currentLocation.isNotEmpty()) {
                binding.tvLocation.text = currentLocation
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopLocationUpdates()
        Glide.with(this).clear(null)
        _binding = null
    }

    private fun updateLocationDisplay(latitude: Double, longitude: Double) {
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val locationName = address.locality ?: address.subAdminArea ?: address.adminArea
                binding.tvLocation.text = locationName
            }
        } catch (e: Exception) {
            Log.e("Location", "Error getting address", e)
        }
    }
} 