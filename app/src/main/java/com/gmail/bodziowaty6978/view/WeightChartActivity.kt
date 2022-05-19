package com.gmail.bodziowaty6978.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityWeightChartBinding
import com.gmail.bodziowaty6978.functions.showSnackbar
import com.gmail.bodziowaty6978.state.Resource
import com.gmail.bodziowaty6978.viewmodel.WeightChartViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeightChartActivity : AppCompatActivity() {
    private lateinit var binding:ActivityWeightChartBinding
    private val viewModel:WeightChartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight_chart)

        binding = ActivityWeightChartBinding.inflate(layoutInflater,null,false)
        setContentView(binding.root)

        viewModel.getWeightEntries()

        lifecycleScope.launchWhenStarted {
            viewModel.weightEntries.observe(this@WeightChartActivity,{
                when(it){
                    is Resource.Error -> showSnackbar(binding.clWeightChart,it.uiText.toString())
                    else -> initializeChart()
                }
            })
        }

        initializeChart()
    }

    private fun initializeChart(){

    }
}