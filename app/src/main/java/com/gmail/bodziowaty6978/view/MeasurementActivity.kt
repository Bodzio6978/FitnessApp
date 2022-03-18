package com.gmail.bodziowaty6978.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityMeasurementBinding
import com.gmail.bodziowaty6978.model.MeasurementEntity
import com.gmail.bodziowaty6978.viewmodel.MeasurementViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MeasurementActivity : AppCompatActivity() {

    lateinit var binding: ActivityMeasurementBinding
    private val viewModel: MeasurementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measurement)

        binding = ActivityMeasurementBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)

        val lastMeasurement = intent.getParcelableExtra<MeasurementEntity>("lastMeasurement")
        if (lastMeasurement != null) {
            viewModel.lastMeasurement = lastMeasurement
            initializeLastValues(lastMeasurement)
        }

        lifecycleScope.launch {
            binding.btSaveMeasurement.setOnClickListener {
                viewModel.addMeasurementEntry(
                    binding.etHipsMeasurement.text.toString(),
                    binding.etWaistMeasurement.text.toString(),
                    binding.etThighMeasurement.text.toString(),
                    binding.etBustMeasurement.text.toString(),
                    binding.etBicepsMeasurement.text.toString(),
                    binding.etCalfMeasurement.text.toString(),
                )
            }
        }
    }

    private fun initializeLastValues(measurement: MeasurementEntity) {
        lifecycleScope.launch {
            binding.etBicepsMeasurement.setText(measurement.biceps.toString())
            binding.etBustMeasurement.setText(measurement.bust.toString())
            binding.etCalfMeasurement.setText(measurement.calf.toString())
            binding.etHipsMeasurement.setText(measurement.hips.toString())
            binding.etThighMeasurement.setText(measurement.thigh.toString())
            binding.etWaistMeasurement.setText(measurement.waist.toString())
        }
    }
}