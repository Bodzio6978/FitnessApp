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

        setUpRefreshLayout()

        observeDate()

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

    private fun setUpRefreshLayout(){
        binding.srlSwipeCalories.setOnRefreshListener {
            viewModel.refresh()
            binding.srlSwipeCalories.isRefreshing = false
        }
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
        viewModel.getProducts().observe(viewLifecycleOwner,{
            if (!it.isNullOrEmpty()){
                for (key in it.keys){
                    when(key){
                        "Breakfast" -> it[key]?.let { it1 -> binding.mvBreakfastCalories.addProducts(it1) }
                        "Lunch" -> it[key]?.let { it1 -> binding.mvLunchCalories.addProducts(it1) }
                        "Dinner" -> it[key]?.let { it1 -> binding.mvDinnerCalories.addProducts(it1) }
                        else -> it[key]?.let { it1 -> binding.mvSupperCalories.addProducts(it1) }
                    }
                }

            }
            calculateValues(it.values.toList())
        })
    }

    private fun calculateValues(list: List<MutableMap<String,JournalEntry>>) {
        val values:MutableMap<String,Int> = mutableMapOf(
                "calories" to 0,
                "carbohydrates" to 0,
                "protein" to 0,
                "fat" to 0
        )

        for(meal in list){
            values["calories"] = values["calories"]!!.plus(meal.values.toList().sumOf(JournalEntry::calories))
            values["carbohydrates"] = values["carbohydrates"]!!.plus(meal.values.toList().sumOf(JournalEntry::carbs).toInt())
            values["protein"] = values["protein"]!!.plus(meal.values.toList().sumOf(JournalEntry::protein).toInt())
            values["fat"] = values["fat"]!!.plus(meal.values.toList().sumOf(JournalEntry::fat).toInt())
        }

        setValues(values)

    }

    private fun setValues(map:Map<String,Int>){
        for (key in map.keys){
            when(key){
                "calories" -> map[key]?.let { binding.nvCalories.updateValue(it) }
                "carbohydrates" -> map[key]?.let { binding.nvCarbohydrates.updateValue(it) }
                "protein" -> map[key]?.let { binding.nvProtein.updateValue(it) }
                "fat" -> map[key]?.let { binding.nvFat.updateValue(it) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}