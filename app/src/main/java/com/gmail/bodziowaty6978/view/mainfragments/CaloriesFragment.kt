package com.gmail.bodziowaty6978.view.mainfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.databinding.FragmentCaloriesBinding
import com.gmail.bodziowaty6978.viewmodel.CaloriesViewModel
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class CaloriesFragment() : Fragment() {

    private var _binding : FragmentCaloriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel:CaloriesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCaloriesBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(CaloriesViewModel::class.java)

        binding.caloriesBreakfast.getValues().observe(viewLifecycleOwner,{
            updateValues(it)
        })

        viewModel.setUpValues()

        viewModel.getValues().observe(viewLifecycleOwner,{
            setUpUI(it)
        })

        viewModel.getJournalProducts()



        return binding.root
    }

    private fun setUpUI(map:Map<*,*>){
        binding.nvCalories.setWanted((map["calories"]).toString().toInt())
        binding.nvCarbohydrates.setWanted((map["carbohydrates"]).toString().toDouble().toInt())
        binding.nvProtein.setWanted((map["protein"]).toString().toDouble().toInt())
        binding.nvFat.setWanted((map["fat"]).toString().toDouble().toInt())

        binding.nvCalories.updateProgress()
        binding.nvCarbohydrates.updateProgress()
        binding.nvProtein.updateProgress()
        binding.nvFat.updateProgress()

    }

    private fun updateValues(list:ArrayList<Int>){
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