package com.example.croppredictor.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.croppredictor.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by viewModels()
    private val adapter = HistoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeHistory()
    }

    private fun setupRecyclerView() {
        try {
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = this@HistoryFragment.adapter
                addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error setting up history view", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeHistory() {
        viewModel.history.observe(viewLifecycleOwner) { historyList ->
            try {
                if (historyList.isNullOrEmpty()) {
                    binding.emptyState.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.emptyState.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    adapter.submitList(historyList)
                }
            } catch (e: Exception) {
                // Log the error and show empty state
                binding.emptyState.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                Toast.makeText(context, "Error loading history", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 