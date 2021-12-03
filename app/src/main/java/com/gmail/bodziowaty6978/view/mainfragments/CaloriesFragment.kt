package com.gmail.bodziowaty6978.view.mainfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.databinding.FragmentCaloriesBinding
import com.gmail.bodziowaty6978.functions.toString
import com.gmail.bodziowaty6978.singleton.CurrentDate
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

        viewModel.setUpValues()

        observeDate()

        observeMealValues()

        observeOverallValues()

        observeProducts()

        observeMealViews()

        observeDeletedProduct()

        return binding.root
    }

    private fun setUpUI(map:Map<*,*>){
        binding.nvCalories.setWanted(((map["wantedCalories"]) as Double).toInt())
        binding.nvCarbohydrates.setWanted(((map["wantedCarbohydrates"]) as Double).toInt())
        binding.nvProtein.setWanted(((map["wantedProtein"]) as Double).toInt())
        binding.nvFat.setWanted(((map["wantedFat"]) as Double).toInt())

        binding.nvCalories.updateProgress()
        binding.nvCarbohydrates.updateProgress()
        binding.nvProtein.updateProgress()
        binding.nvFat.updateProgress()
    }

    private fun observeDate(){
        CurrentDate.date.observe(viewLifecycleOwner,{
            viewModel.getJournalEntries(it.time.toString("yyyy-MM-dd"))
        })
    }

    private fun observeDeletedProduct(){
        viewModel.getDeleteProduct().observe(viewLifecycleOwner,{
            deletedProduct(it)
        })

    }

    private fun observeOverallValues(){
        viewModel.getValues().observe(viewLifecycleOwner,{
            setUpUI(it)
        })
    }

    private fun observeMealValues(){
        binding.mvBreakfastCalories.getValues().observe(viewLifecycleOwner,{
            updateValues(it)
        })

        binding.mvLunchCalories.getValues().observe(viewLifecycleOwner,{
            updateValues(it)
        })

        binding.mvDinnerCalories.getValues().observe(viewLifecycleOwner,{
            updateValues(it)
        })

        binding.mvSupperCalories.getValues().observe(viewLifecycleOwner,{
            updateValues(it)
        })
    }

    private fun observeMealViews(){
        binding.mvBreakfastCalories.getDeletedProduct().observe(viewLifecycleOwner,{
            viewModel.removeItem(it.first,it.second)
        })
        binding.mvLunchCalories.getDeletedProduct().observe(viewLifecycleOwner,{
            viewModel.removeItem(it.first,it.second)
        })
        binding.mvDinnerCalories.getDeletedProduct().observe(viewLifecycleOwner,{
            viewModel.removeItem(it.first,it.second)
        })
        binding.mvSupperCalories.getDeletedProduct().observe(viewLifecycleOwner,{
            viewModel.removeItem(it.first,it.second)
        })
    }

    private fun observeProducts(){
        viewModel.getBreakfastProducts().observe(viewLifecycleOwner,{
            binding.mvBreakfastCalories.addProducts(it)
        })

        viewModel.getLunchProducts().observe(viewLifecycleOwner,{
            binding.mvLunchCalories.addProducts(it)
        })

        viewModel.getDinnerProducts().observe(viewLifecycleOwner,{
            binding.mvDinnerCalories.addProducts(it)
        })

        viewModel.getSupperProducts().observe(viewLifecycleOwner,{
            binding.mvSupperCalories.addProducts(it)
        })
    }

    private fun deletedProduct(productInformation:ArrayList<String>){
        when(productInformation[1]){
            "Breakfast" -> binding.mvBreakfastCalories.removeProduct(productInformation[0].toInt())
            "Lunch" -> binding.mvLunchCalories.removeProduct(productInformation[0].toInt())
            "Dinner" -> binding.mvDinnerCalories.removeProduct(productInformation[0].toInt())
            "Supper" -> binding.mvSupperCalories.removeProduct(productInformation[0].toInt())
        }
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