package com.gmail.bodziowaty6978.view.mainfragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
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
import com.gmail.bodziowaty6978.functions.TAG
import com.gmail.bodziowaty6978.functions.toString
import com.gmail.bodziowaty6978.model.JournalEntry
import com.gmail.bodziowaty6978.singleton.CurrentDate
import com.gmail.bodziowaty6978.singleton.UserInformation
import com.gmail.bodziowaty6978.viewmodel.DiaryViewModel

class DiaryFragment() : Fragment() {

    private var _binding: FragmentCaloriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DiaryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCaloriesBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(DiaryViewModel::class.java)

        setUpRefreshLayout()

        observeDate()

        observeWantedValues()

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
            viewModel.removeItem(entry,mealName)
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

//    private fun editEntry(position: Int,mealName: String){
//        val entry = when(mealName){
//            "Breakfast" -> binding.mvBreakfastCalories.getEntry(position)
//            "Lunch" -> binding.mvLunchCalories.getEntry(position)
//            "Dinner" -> binding.mvDinnerCalories.getEntry(position)
//            else -> binding.mvSupperCalories.getEntry(position)
//        }
//
//        val intent = Intent(requireContext(),ProductActivity::class.java).putExtra("mealName",mealName)
//
//    }

    private fun observeWantedValues(){
        UserInformation.mValues.observe(viewLifecycleOwner,{
            if (it.isNotEmpty()){
                setUpUI(it)
            }
        })
    }

    private fun setUpUI(map: Map<String, Double>) {
        binding.nvCalories.setWanted(((map["wantedCalories"])?.toInt()))
        binding.nvCarbohydrates.setWanted((map["wantedCarbohydrates"])?.toInt())
        binding.nvProtein.setWanted((map["wantedProtein"])?.toInt())
        binding.nvFat.setWanted((map["wantedFat"])?.toInt())
    }

    private fun observeDate() {
        CurrentDate.date.observe(viewLifecycleOwner, {
            viewModel.getJournalEntries(it.time.toString("yyyy-MM-dd"))
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
                Log.e(TAG,"huj")
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