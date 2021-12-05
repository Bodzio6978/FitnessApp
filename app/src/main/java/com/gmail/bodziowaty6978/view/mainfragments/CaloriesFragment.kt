package com.gmail.bodziowaty6978.view.mainfragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.FragmentCaloriesBinding
import com.gmail.bodziowaty6978.functions.toString
import com.gmail.bodziowaty6978.model.JournalEntry
import com.gmail.bodziowaty6978.singleton.CurrentDate
import com.gmail.bodziowaty6978.viewmodel.CaloriesViewModel

class CaloriesFragment() : Fragment() {

    private var _binding: FragmentCaloriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CaloriesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCaloriesBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(CaloriesViewModel::class.java)

        viewModel.setUpValues()

        observeDate()

        observeMealValues()

        observeOverallValues()

        observeProducts()

        observeLongClickedEntry()

        return binding.root
    }

    private fun showEntryDialog(position: Int, mealName: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.entry_dialog_layout)
        val entryName = dialog.findViewById(R.id.tvEntryNameDialog) as TextView

        val entry = getEntry(position,mealName)

        entryName.text = entry.name

        val delete = dialog.findViewById(R.id.btDeleteDialog) as Button

        delete.setOnClickListener{
            viewModel.removeItem(entry,mealName,position)
            dialog.dismiss()
        }

        dialog.show()

    }

//    private fun startProductActivity(position: Int,mealName: String){
//        val entry = viewModel.getJournalEntry(position: Int,mealName: String)
//    }

    private fun setUpUI(map: Map<*, *>) {
        binding.nvCalories.setWanted(((map["wantedCalories"]) as Double).toInt())
        binding.nvCarbohydrates.setWanted(((map["wantedCarbohydrates"]) as Double).toInt())
        binding.nvProtein.setWanted(((map["wantedProtein"]) as Double).toInt())
        binding.nvFat.setWanted(((map["wantedFat"]) as Double).toInt())

        binding.nvCalories.updateProgress()
        binding.nvCarbohydrates.updateProgress()
        binding.nvProtein.updateProgress()
        binding.nvFat.updateProgress()
    }

    private fun observeDate() {
        CurrentDate.date.observe(viewLifecycleOwner, {
            viewModel.getJournalEntries(it.time.toString("yyyy-MM-dd"))
        })
    }


    private fun observeOverallValues() {
        viewModel.getValues().observe(viewLifecycleOwner, {
            setUpUI(it)
        })
    }

    private fun observeMealValues() {
        binding.mvBreakfastCalories.getValues().observe(viewLifecycleOwner, {
            updateValues(it)
        })

        binding.mvLunchCalories.getValues().observe(viewLifecycleOwner, {
            updateValues(it)
        })

        binding.mvDinnerCalories.getValues().observe(viewLifecycleOwner, {
            updateValues(it)
        })

        binding.mvSupperCalories.getValues().observe(viewLifecycleOwner, {
            updateValues(it)
        })
    }

    private fun getEntry(position: Int,mealName: String):JournalEntry{
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
        viewModel.getBreakfastProducts().observe(viewLifecycleOwner, {
            binding.mvBreakfastCalories.addProducts(it)
            binding.mvBreakfastCalories.updateValues(viewModel.getBreakfastValues())
        })

        viewModel.getLunchProducts().observe(viewLifecycleOwner, {
            binding.mvLunchCalories.addProducts(it)
            binding.mvLunchCalories.updateValues(viewModel.getLunchValues())
        })

        viewModel.getDinnerProducts().observe(viewLifecycleOwner, {
            binding.mvDinnerCalories.addProducts(it)
            binding.mvDinnerCalories.updateValues(viewModel.getDinnerValues())
        })

        viewModel.getSupperProducts().observe(viewLifecycleOwner, {
            binding.mvSupperCalories.addProducts(it)
            binding.mvSupperCalories.updateValues(viewModel.getSupperValues())
        })
    }

    private fun updateValues(list: ArrayList<Int>) {
        binding.nvCalories.updateValue(list[0])
        binding.nvCarbohydrates.updateValue(list[1])
        binding.nvProtein.updateValue(list[2])
        binding.nvFat.updateValue(list[3])
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}