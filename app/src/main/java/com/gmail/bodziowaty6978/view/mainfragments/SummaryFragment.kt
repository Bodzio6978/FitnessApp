package com.gmail.bodziowaty6978.view.mainfragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.FragmentSummaryBinding
import com.gmail.bodziowaty6978.functions.TAG
import com.gmail.bodziowaty6978.functions.getMeasurementMap
import com.gmail.bodziowaty6978.functions.showSnackbar
import com.gmail.bodziowaty6978.model.JournalEntry
import com.gmail.bodziowaty6978.model.MeasurementEntity
import com.gmail.bodziowaty6978.model.WeightEntity
import com.gmail.bodziowaty6978.state.DataState
import com.gmail.bodziowaty6978.state.Resource
import com.gmail.bodziowaty6978.view.MeasurementActivity
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
            viewModel.dataState.collect { it ->
                if (it is DataState.Success) {
                    setLogStrike()

                    observeLastWeight()

                    observeCurrentUser()

                    observeLogEntry()

                    observeCurrentCalories()

                    observeMeasurementEntries()

                    lifecycleScope.launch {
                        binding.ibAddMeasurementSummary.setOnClickListener {
                            val lastEntries = viewModel.measurementEntries.value!!

                            val intent = Intent(requireActivity(),MeasurementActivity::class.java)

                            if (lastEntries.isNotEmpty()){
                                lastEntries.sortBy { entry -> entry.time }
                                intent.putExtra("lastMeasurement",lastEntries[lastEntries.size-1])
                            }

                            startActivity(intent)
                        }
                    }
                }
            }
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeMeasurementEntries(){
        lifecycleScope.launchWhenStarted {
            viewModel.measurementEntries.observe(viewLifecycleOwner,{
                binding.ibAddMeasurementSummary.visibility = View.VISIBLE

                if (it.isEmpty()){
                    binding.llMeasurements.visibility = View.GONE
                    binding.tvNotEnteredMeasurement.visibility = View.VISIBLE
                }else{
                    it.sortBy { item -> item.time }
                    initializeMeasurementUi(it)
                }
            })
        }
    }

    private fun initializeMeasurementUi(measurementEntities:MutableList<MeasurementEntity>){
        if (measurementEntities.size==1){
            val entity = measurementEntities[0]
            setMeasurementValues(entity.getMeasurementMap())
        }else{
            val progress = viewModel.calculateProgress(measurementEntities)
            setMeasurementValues(progress)
        }
    }

    private fun setMeasurementValues(values:Map<String,String>){
        binding.tvHipsSummary.text = String.format(resources.getString(R.string.hips_value),values["hips"])
        binding.tvWaistSummary.text = String.format(resources.getString(R.string.waist_value),values["waist"])
        binding.tvThighSummary.text = String.format(resources.getString(R.string.thigh_value),values["thigh"])
        binding.tvBustSummary.text = String.format(resources.getString(R.string.bust_value),values["bust"])
        binding.tvBicepsSummary.text = String.format(resources.getString(R.string.biceps_value),values["biceps"])
        binding.tvCalfSummary.text = String.format(resources.getString(R.string.calf_value),values["calf"])
    }

    private fun observeLogEntry(){
        lifecycleScope.launchWhenStarted {
            viewModel.logEntries.observe(viewLifecycleOwner,{
                if (it.isNotEmpty()){
                    viewModel.calculateStrike(it[0])
                }else{
                    lifecycleScope.launch {
                        viewModel.createLogEntry()
                    }
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
                Log.e(TAG,it.toString())
                setWeight(it.toMutableList())
            })
        }
    }

    private fun setWeight(weights: MutableList<WeightEntity>) {
        if (weights.isEmpty()) {
            val userInformation = viewModel.userInformation.value!!.userInformation!!
            binding.tvCurrentWeightSummary.text = ("${userInformation["currentWeight"]} kg")
        } else {
            lifecycleScope.launch {
                withContext(Dispatchers.Default) {
                    weights.sortByDescending { it.time }
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
            viewModel.userInformation.observe(viewLifecycleOwner, {
                val nutritionValues = it.nutritionValues
                if (nutritionValues != null) {
                    setWantedCalories(nutritionValues["wantedCalories"]!!)
                }
            })
        }
    }

    private fun observeCurrentCalories() {
        lifecycleScope.launch {
            viewModel.journalEntries.observe(viewLifecycleOwner, {
                when(it){
                    is Resource.Success -> onJournalSuccess(it.data!!)
                    else -> showSnackbar(binding.clSummary,it.uiText.toString())
                }
            })
        }
    }

    private fun onJournalSuccess(data:MutableMap<String,MutableMap<String,JournalEntry>>){
        setCurrentCalories(viewModel.getEntries(data.values.toList()))
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
