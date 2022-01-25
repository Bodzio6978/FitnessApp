package com.gmail.bodziowaty6978.view.mainfragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.databinding.FragmentSummaryBinding
import com.gmail.bodziowaty6978.functions.TAG
import com.gmail.bodziowaty6978.functions.round
import com.gmail.bodziowaty6978.functions.toCalendar
import com.gmail.bodziowaty6978.functions.toShortString
import com.gmail.bodziowaty6978.model.JournalEntry
import com.gmail.bodziowaty6978.model.LogEntry
import com.gmail.bodziowaty6978.model.WeightEntry
import com.gmail.bodziowaty6978.singleton.UserInformation
import com.gmail.bodziowaty6978.viewmodel.MainViewModel
import java.util.*


class SummaryFragment : Fragment() {

    private var _binding: FragmentSummaryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentSummaryBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        viewModel.getWeightEntries()

        viewModel.getLastTimeLogged(1)

        observeLastTimeLogged()
        setLogStrike()

        observeLastWeight()

        observeWantedCalories()

        observeCurrentCalories()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setLogStrike() {
        viewModel.mCurrentLogStrike.observe(viewLifecycleOwner, {
            "$it days".also { binding.tvDaysLoggedSummary.text = it }
        })
    }

    private fun observeLastTimeLogged() {
        viewModel.mLastLogEntry.observe(viewLifecycleOwner, {
            calculateStrike(it)
        })
    }

    private fun calculateStrike(entry: LogEntry) {
        val currentDate = Calendar.getInstance()
        val lastDateCalendar = toCalendar(Date(entry.time))

        if (lastDateCalendar != null) {

            val lastTimeLogged = lastDateCalendar.toShortString()

            if (lastTimeLogged != currentDate.toShortString()) {
                lastDateCalendar.add(Calendar.DAY_OF_MONTH, 1)

                if (lastDateCalendar.toShortString() == currentDate.toShortString()) {
                    Log.e(TAG,"User logged for another day in a row")
                    viewModel.addLogEntry(currentDate, entry.strike + 1)
                }else{
                    Log.e(TAG,"User logged again but not for another day in a row")
                    viewModel.addLogEntry()
                }
            } else {
                Log.e(TAG,"entry for this day has been already entered")
                viewModel.mCurrentLogStrike.value = entry.strike
            }
        }
    }

    private fun observeLastWeight() {
        viewModel.mLastWeights.observe(viewLifecycleOwner, {
            setWeight(it)
        })
    }

    private fun setWeight(weights: MutableList<WeightEntry>) {
        if (weights.isEmpty()) {
            val userInformation = UserInformation.mUserInformation.value!!
            binding.tvCurrentWeightSummary.text = ("${userInformation["currentWeight"]} kg")
        } else {
            weights.sortByDescending { it.time }
            binding.tvCurrentWeightSummary.text = ("${weights[0].value} kg")
            updateWeightProgress(weights)
        }
    }

    private fun updateWeightProgress(weights: MutableList<WeightEntry>) {
        if (weights.size > 1) {
            val size = weights.size

            val firstHalf = weights.toTypedArray().copyOfRange(0, (size + 1) / 2)
            val secondHalf = weights.toTypedArray().copyOfRange((size + 1) / 2, size)

            val firstAverage = firstHalf.toMutableList().sumOf { it.value } / firstHalf.size.toDouble()
            val secondAverage = secondHalf.toMutableList().sumOf { it.value } / secondHalf.size.toDouble()

            val difference = (firstAverage - secondAverage).round(2)
            val sign = if (firstAverage > secondAverage) "+" else ""

            if (difference != 0.0) ("$sign${difference}kg").also { binding.tvWeightChangeSummary.text = it }
        }
    }

    private fun observeWantedCalories() {
        UserInformation.mNutritionValues.observe(viewLifecycleOwner, {
            setWantedCalories(it["wantedCalories"]!!)
        })
    }

    private fun observeCurrentCalories(){
        viewModel.mJournalEntries.observe(viewLifecycleOwner,{
            getEntries(it.values.toList())
        })
    }

    private fun getEntries(entries:List<MutableMap<String,JournalEntry>>){
        
    }

    private fun setCurrentCalories(entries: MutableList<JournalEntry>) {
        if (entries.isNotEmpty()) {
            val caloriesSum = entries.sumOf { journalEntry: JournalEntry -> journalEntry.calories }
            binding.tvCurrentCaloriesSummary.text = "$caloriesSum"
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
