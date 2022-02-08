package com.gmail.bodziowaty6978.view.mainfragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gmail.bodziowaty6978.databinding.FragmentSummaryBinding
import com.gmail.bodziowaty6978.functions.TAG
import com.gmail.bodziowaty6978.model.JournalEntry
import com.gmail.bodziowaty6978.model.WeightEntry
import com.gmail.bodziowaty6978.singleton.UserInformation
import com.gmail.bodziowaty6978.state.DataState
import com.gmail.bodziowaty6978.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SummaryFragment : Fragment() {

    private var _binding: FragmentSummaryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSummaryBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        lifecycleScope.launchWhenStarted {
            viewModel.dataState.collect {
                if (it is DataState.Success) {
                    setLogStrike()

                    observeLastWeight()

                    observeCurrentUser()

                    observeLogEntry()

                    observeCurrentCalories()
                }
            }
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeLogEntry(){
        lifecycleScope.launchWhenStarted {
            viewModel.logEntries.observe(viewLifecycleOwner,{
                if (it.isNotEmpty()){
                    viewModel.calculateStrike(it[0])
                }
            })
        }
    }

    private fun setLogStrike() {
        lifecycleScope.launchWhenStarted {
            viewModel.currentLogStrike.collect { logStrike ->
                "$logStrike days".also { binding.tvDaysLoggedSummary.text = it }
            }
        }
    }

    private fun observeLastWeight() {
        lifecycleScope.launchWhenStarted {
            viewModel.weightEntries.observe(viewLifecycleOwner, {
                Log.e(TAG, "Collected")
                setWeight(it.toMutableList())
            })
        }
    }

    private fun setWeight(weights: MutableList<WeightEntry>) {
        if (weights.isEmpty()) {
            val userInformation = UserInformation.user.value!!.userInformation!!
            binding.tvCurrentWeightSummary.text = ("${userInformation["currentWeight"]} kg")
        } else {
            lifecycleScope.launch {
                withContext(Dispatchers.Default) {
                    weights.sortByDescending { it.time }
                    Log.e(TAG, weights.toString())
                    val progress = viewModel.calculateWeightProgress(weights)

                    withContext(Dispatchers.Main) {
                        if (weights.size > 0) {
                            "${weights[0].value}kg".also {
                                binding.tvCurrentWeightSummary.text = it
                            }
                        }
                        binding.tvWeightChangeSummary.text = progress
                    }
                }
            }
        }
    }


    private fun observeCurrentUser() {
        lifecycleScope.launchWhenStarted {
            UserInformation.user.observe(viewLifecycleOwner, {
                val nutritionValues = it.nutritionValues
                if (nutritionValues != null) {
                    setWantedCalories(nutritionValues["wantedCalories"]!!)
                }
            })
        }
    }

    private fun observeCurrentCalories() {
        lifecycleScope.launchWhenStarted {
            viewModel.journalEntries.observe(viewLifecycleOwner, {
                setCurrentCalories(viewModel.getEntries(it.values.toList()))
            })
        }
    }

    private fun setCurrentCalories(entries: List<JournalEntry>) {
        lifecycleScope.launch {
            withContext(Dispatchers.Default) {
                val caloriesSum = entries.sumOf { journalEntry: JournalEntry -> journalEntry.calories }
                withContext(Dispatchers.Main) {
                    binding.tvCurrentCaloriesSummary.text = "$caloriesSum"
                }
            }
        }
    }

    private fun setWantedCalories(value: Double) {
        "${value.toInt()}".also { binding.tvWantedCaloriesSummary.text = it }
        updateProgress()
    }

    private fun updateProgress() {
        val current = binding.tvCurrentCaloriesSummary.text.toString().toInt().toDouble()
        val wanted = binding.tvWantedCaloriesSummary.text.toString().toInt().toDouble()

        if (wanted != 0.0) {
            val progress = (current / wanted * 100.0).toInt()
            binding.pbCaloriesSummary.progress = progress
            "$progress%".also { binding.tvCaloriesProgress.text = it }
        }

    }


}
