package com.gmail.bodziowaty6978.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.databinding.FragmentCaloriesBinding
import com.gmail.bodziowaty6978.model.Meal
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

        binding.nvCalories.setWanted(2500)
        binding.nvCarbohydrates.setWanted(220)
        binding.nvProtein.setWanted(180)
        binding.nvFat.setWanted(100)

        binding.caloriesBreakfast.addMeal(Meal("huj","Szynka zawedzana","Biedronka","115",2,"192","1","20","12"))
        binding.caloriesBreakfast.addMeal(Meal("huj","Nutella","Biedronka","115",2,"546","57","6","30"))


        return binding.root
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