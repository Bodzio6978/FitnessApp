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
import com.gmail.bodziowaty6978.databinding.FragmentCaloriesBinding
import com.gmail.bodziowaty6978.functions.TAG
import com.gmail.bodziowaty6978.functions.showSnackbar
import com.gmail.bodziowaty6978.functions.toShortString
import com.gmail.bodziowaty6978.model.JournalEntry
import com.gmail.bodziowaty6978.singleton.CurrentDate
import com.gmail.bodziowaty6978.singleton.UserInformation
import com.gmail.bodziowaty6978.state.DataState
import com.gmail.bodziowaty6978.state.Resource
import com.gmail.bodziowaty6978.view.ProductActivity
import com.gmail.bodziowaty6978.viewmodel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class DiaryFragment() : Fragment() {

    private var _binding: FragmentCaloriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCaloriesBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        lifecycleScope.launchWhenStarted {
            viewModel.dataState.collect {
                if (it is DataState.Success) {
                    setUpRefreshLayout()

                    observeWantedValues()

                    observeProducts()

                    observeLongClickedEntry()
                }
            }
        }
        return binding.root
    }


    private fun showEntryDialog(position: Int, mealName: String) {

        val entry = getEntry(position, mealName)

        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("${entry.name} (${entry.weight}${entry.unit})")

            setNegativeButton(resources.getString(R.string.delete)) { _, _ ->
                viewModel.removeItem(entry, mealName)
            }

            setNeutralButton(resources.getString(R.string.edit)) { _, _ ->

            }
            show()
        }
    }


    private fun setUpRefreshLayout() {
        binding.srlSwipeCalories.setOnRefreshListener {
            viewModel.refreshJournalEntries(CurrentDate.date().value!!.toShortString())
            binding.srlSwipeCalories.isRefreshing = false
        }
    }

    private fun editEntry(position: Int, mealName: String) {
        val entry = when (mealName) {
            "Breakfast" -> binding.mvBreakfastCalories.getEntry(position)
            "Lunch" -> binding.mvLunchCalories.getEntry(position)
            "Dinner" -> binding.mvDinnerCalories.getEntry(position)
            else -> binding.mvSupperCalories.getEntry(position)
        }

        val intent =
            Intent(requireContext(), ProductActivity::class.java).putExtra("mealName", mealName)
    }

    private fun observeWantedValues() {
        UserInformation.user.observe(viewLifecycleOwner, {
            setUpUI(it.nutritionValues!!)
        })
    }

    private fun setUpUI(map: Map<String, Double>) {
        binding.nvCalories.setWanted(((map["wantedCalories"])?.toInt()))
        binding.nvCarbohydrates.setWanted((map["wantedCarbohydrates"])?.toInt())
        binding.nvProtein.setWanted((map["wantedProtein"])?.toInt())
        binding.nvFat.setWanted((map["wantedFat"])?.toInt())
    }

    private fun getEntry(position: Int, mealName: String): JournalEntry {
        return when (mealName) {
            "Breakfast" -> binding.mvBreakfastCalories.getEntry(position)
            "Lunch" -> binding.mvLunchCalories.getEntry(position)
            "Dinner" -> binding.mvDinnerCalories.getEntry(position)
            else -> binding.mvSupperCalories.getEntry(position)
        }
    }

    private fun observeLongClickedEntry() {
        binding.mvBreakfastCalories.getClickedEntry().observe(viewLifecycleOwner, {
            showEntryDialog(it.first, it.second)
        })
        binding.mvLunchCalories.getClickedEntry().observe(viewLifecycleOwner, {
            showEntryDialog(it.first, it.second)
        })
        binding.mvDinnerCalories.getClickedEntry().observe(viewLifecycleOwner, {
            showEntryDialog(it.first, it.second)
        })
        binding.mvSupperCalories.getClickedEntry().observe(viewLifecycleOwner, {
            showEntryDialog(it.first, it.second)
        })
    }

    private fun observeProducts() {
        lifecycleScope.launch {
            viewModel.journalEntries.observe(viewLifecycleOwner, {
                when(it){
                    is Resource.Success -> onJournalSuccess(it.data!!)
                    else -> showSnackbar(binding.clJournal,it.uiText.toString())
                }
            })
        }
    }

    private fun onJournalSuccess(data:MutableMap<String,MutableMap<String,JournalEntry>>){
        val breakfast = data["Breakfast"]
        val lunch = data["Lunch"]
        val dinner = data["Dinner"]
        val supper = data["Supper"]

        binding.mvBreakfastCalories.setProducts(breakfast)
        binding.mvLunchCalories.setProducts(lunch)
        binding.mvDinnerCalories.setProducts(dinner)
        binding.mvSupperCalories.setProducts(supper)

        Log.e(TAG,viewModel.getEntries(data.values.toList()).toString())

        calculateValues(viewModel.getEntries(data.values.toList()))
    }

    private fun calculateValues(list: List<JournalEntry>) {
        binding.nvCalories.updateValue(list.sumOf { journalEntry: JournalEntry -> journalEntry.calories })
        binding.nvCarbohydrates.updateValue(list.sumOf { journalEntry: JournalEntry -> journalEntry.carbs }
            .toInt())
        binding.nvProtein.updateValue(list.sumOf { journalEntry: JournalEntry -> journalEntry.protein }
            .toInt())
        binding.nvFat.updateValue(list.sumOf { journalEntry: JournalEntry -> journalEntry.fat }
            .toInt())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}