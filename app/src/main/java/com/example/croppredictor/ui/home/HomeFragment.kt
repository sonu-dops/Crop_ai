package com.example.croppredictor.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.croppredictor.R
import com.example.croppredictor.databinding.FragmentHomeBinding
import com.example.croppredictor.utils.openGeminiAI

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.btnStartPrediction.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_predictionFragment)
        }

        binding.btnHowAppWorks.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_howItWorksFragment)
        }

        binding.btnFarmingAnalysis.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_homeFragment_to_farmingAnalysisFragment)
            } catch (e: Exception) {
                Toast.makeText(context, "Unable to open analysis page", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 