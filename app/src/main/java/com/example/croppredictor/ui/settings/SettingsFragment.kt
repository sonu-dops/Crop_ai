package com.example.croppredictor.ui.settings

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.croppredictor.R
import com.example.croppredictor.databinding.FragmentSettingsBinding
import java.util.Locale

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()
    private var isInitialSetup = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLanguageSpinner()
        setupThemeOptions()
        setupSaveButton()
        setupAdditionalSettings()
    }

    private fun setupLanguageSpinner() {
        val languages = arrayOf("English", "हिंदी", "বাংলা")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        
        binding.spinnerLanguage.adapter = adapter
        
        // Set initial selection without triggering listener
        val currentLang = viewModel.getLanguage()
        val position = when(currentLang) {
            "hi" -> 1
            "bn" -> 2
            else -> 0
        }
        binding.spinnerLanguage.setSelection(position, false)

        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!isInitialSetup) {
                    val locale = when(position) {
                        1 -> "hi"
                        2 -> "bn"
                        else -> "en"
                    }
                    if (locale != viewModel.getLanguage()) {
                        setLocale(locale)
                    }
                }
                isInitialSetup = false
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupThemeOptions() {
        // Set initial theme selection
        val currentTheme = viewModel.getThemeMode() // Get from saved preferences
        val radioButtonId = when (currentTheme) {
            AppCompatDelegate.MODE_NIGHT_YES -> R.id.radioDark
            AppCompatDelegate.MODE_NIGHT_NO -> R.id.radioLight
            else -> R.id.radioSystem
        }
        binding.radioGroupTheme.check(radioButtonId)

        binding.radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                R.id.radioDark -> AppCompatDelegate.MODE_NIGHT_YES
                R.id.radioLight -> AppCompatDelegate.MODE_NIGHT_NO
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            if (mode != viewModel.getThemeMode()) {
                viewModel.saveThemeMode(mode)
                // Apply theme change immediately
                AppCompatDelegate.setDefaultNightMode(mode)
            }
        }
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            // Save language
            val selectedLanguage = when(binding.spinnerLanguage.selectedItemPosition) {
                1 -> "hi"
                2 -> "bn"
                else -> "en"
            }
            val languageChanged = selectedLanguage != viewModel.getLanguage()
            viewModel.saveLanguage(selectedLanguage)

            // Save other settings
            val selectedTheme = when(binding.radioGroupTheme.checkedRadioButtonId) {
                R.id.radioDark -> AppCompatDelegate.MODE_NIGHT_YES
                R.id.radioLight -> AppCompatDelegate.MODE_NIGHT_NO
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            viewModel.saveThemeMode(selectedTheme)

            Toast.makeText(requireContext(), R.string.settings_saved, Toast.LENGTH_SHORT).show()
            
            // Only recreate if language changed
            if (languageChanged) {
                activity?.recreate()
            }
        }
    }

    private fun setupAdditionalSettings() {
        // Data Saving Mode
        binding.switchDataSaving.isChecked = viewModel.isDataSavingEnabled()
        binding.switchDataSaving.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDataSavingEnabled(isChecked)
        }

        // Weather Update Interval
        binding.sliderWeatherInterval.value = viewModel.getWeatherUpdateInterval().toFloat()
        binding.sliderWeatherInterval.addOnChangeListener { _, value, _ ->
            binding.tvWeatherInterval.text = getString(R.string.weather_interval_format, value.toInt())
            viewModel.setWeatherUpdateInterval(value.toInt())
        }

        // Unit System
        val unitSystem = viewModel.getUnitSystem()
        binding.radioGroupUnits.check(
            when (unitSystem) {
                "imperial" -> R.id.radioImperial
                else -> R.id.radioMetric
            }
        )
        binding.radioGroupUnits.setOnCheckedChangeListener { _, checkedId ->
            val system = when (checkedId) {
                R.id.radioImperial -> "imperial"
                else -> "metric"
            }
            viewModel.setUnitSystem(system)
        }

        // Notifications
        binding.switchNotifications.isChecked = viewModel.isNotificationsEnabled()
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setNotificationsEnabled(isChecked)
        }
    }

    private fun setLocale(languageCode: String) {
        viewModel.saveLanguage(languageCode)
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        activity?.let { activity ->
            val config = Configuration(activity.resources.configuration)
            config.setLocale(locale)
            activity.createConfigurationContext(config)
            activity.recreate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 