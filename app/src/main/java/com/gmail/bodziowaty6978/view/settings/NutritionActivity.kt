package com.gmail.bodziowaty6978.view.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.NumberPicker
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityNutritionBinding
import com.gmail.bodziowaty6978.functions.round
import com.gmail.bodziowaty6978.functions.showSnackbar
import com.gmail.bodziowaty6978.interfaces.DispatcherProvider
import com.gmail.bodziowaty6978.model.User
import com.gmail.bodziowaty6978.state.DataState
import com.gmail.bodziowaty6978.view.MainActivity
import com.gmail.bodziowaty6978.viewmodel.NutritionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class NutritionActivity : AppCompatActivity(), NumberPicker.OnValueChangeListener {

    @Inject
    lateinit var dispatchers: DispatcherProvider

    lateinit var binding: ActivityNutritionBinding
    private val viewModel: NutritionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutrition)

        binding = ActivityNutritionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            viewModel.userInformation.observe(this@NutritionActivity, {
                initializeUserSettings(it)
            })
        }

        lifecycleScope.launch {
            binding.btSaveNutrition.setOnClickListener {
                viewModel.saveSettings(
                    binding.etCaloriesNutrition.text.toString(),
                    getPercentages()
                )
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.settingsState.observe(this@NutritionActivity, {
                when (it) {
                    is DataState.Error -> {
                        showSnackbar(binding.clNutrition, it.errorMessage)
                        binding.rlNutrition.visibility = View.VISIBLE
                        binding.pbNutrition.visibility = View.GONE
                    }
                    is DataState.Success -> startActivity(Intent(this@NutritionActivity,MainActivity::class.java))
                    else -> {
                        binding.rlNutrition.visibility = View.GONE
                        binding.pbNutrition.visibility = View.VISIBLE
                    }
                }
            })
        }

        lifecycleScope.launch {
            binding.etCaloriesNutrition.doAfterTextChanged {
                calculateValues(it.toString())
            }
        }

        setUpNumberPickers(
            formatToString = { "$it%" }
        )
    }

    private fun initializeUserSettings(user: User) {
        lifecycleScope.launch {
            val nutritionValues = user.nutritionValues
            binding.etCaloriesNutrition.setText(
                nutritionValues!!["wantedCalories"]!!.toInt().toString()
            )

            withContext(dispatchers.default) {
                calculatePercentages(user.nutritionValues)
            }
        }

    }

    private fun calculatePercentages(nutritionValues: Map<String, Double>) {
        val calories = nutritionValues["wantedCalories"]!!
        val carbs = nutritionValues["wantedCarbohydrates"]!!
        val protein = nutritionValues["wantedProtein"]!!

        val carbohydratesPercentage = (carbs * 4.0 * 100.0 / calories).roundToInt()
        val proteinPercentage = (protein * 4.0 * 100.0 / calories).roundToInt()
        val fatPercentage = (100.0 - carbohydratesPercentage - proteinPercentage).roundToInt()

        val caloriesInt = calories.toInt()

        binding.npCarbohydratesNutrition.value = carbohydratesPercentage
        binding.npProteinNutrition.value = proteinPercentage
        binding.npFatNutrition.value = fatPercentage

        setValues(
            caloriesInt.toString(), mapOf(
                "carbs" to carbohydratesPercentage,
                "protein" to proteinPercentage,
                "fat" to fatPercentage
            )
        )
    }

    private fun setUpNumberPickers(
        formatToString: (Int) -> String
    ) {
        binding.npCarbohydratesNutrition.apply {
            setFormatter { formatToString(it) }
            wrapSelectorWheel = false

            minValue = (5)
            maxValue = (90)
            this.value = 45

            setOnValueChangedListener(this@NutritionActivity)

            (NumberPicker::class.java.getDeclaredField("mInputText").apply { isAccessible = true }
                .get(this) as EditText).filters = emptyArray()
        }

        binding.npProteinNutrition.apply {
            setFormatter { formatToString(it) }
            wrapSelectorWheel = false

            minValue = (5)
            maxValue = (90)
            this.value = 30

            setOnValueChangedListener(this@NutritionActivity)

            (NumberPicker::class.java.getDeclaredField("mInputText").apply { isAccessible = true }
                .get(this) as EditText).filters = emptyArray()
        }

        binding.npFatNutrition.apply {
            setFormatter { formatToString(it) }
            wrapSelectorWheel = false

            minValue = (5)
            maxValue = (90)
            this.value = 25

            setOnValueChangedListener(this@NutritionActivity)

            (NumberPicker::class.java.getDeclaredField("mInputText").apply { isAccessible = true }
                .get(this) as EditText).filters = emptyArray()
        }
    }

    private fun calculateValues(calories: String) {
        updateTotalPercentage()
        if (calories.isBlank()) {
            binding.tvCurrentCarbsNutrition.text = ""
            binding.tvCurrentFatNutrition.text = ""
            binding.tvCurrentProteinNutrition.text = ""
        } else {
            val percentages = getPercentages()
            setValues(calories, percentages)
        }
    }

    private fun getPercentages(): Map<String, Int> {
        val carbs = binding.npCarbohydratesNutrition.value
        val protein = binding.npProteinNutrition.value
        val fat = binding.npFatNutrition.value
        return mapOf(
            "carbs" to carbs,
            "protein" to protein,
            "fat" to fat
        )
    }

    private fun setValues(calories: String, percentages: Map<String, Int>) {
        val caloriesInt = calories.toInt().toDouble()
        val carbsValue = (percentages["carbs"]!!.toDouble() / 100 * caloriesInt / 4.0).round(2)
        val proteinValue = (percentages["protein"]!!.toDouble() / 100 * caloriesInt / 4.0).round(2)
        val fatValue = (percentages["fat"]!!.toDouble() / 100 * caloriesInt / 9.0).round(2)

        "${carbsValue}g".also { binding.tvCurrentCarbsNutrition.text = it }
        "${proteinValue}g".also { binding.tvCurrentProteinNutrition.text = it }
        "${fatValue}g".also { binding.tvCurrentFatNutrition.text = it }
    }

    private fun updateTotalPercentage() {
        val percentages = getPercentages()
        val sum = percentages.values.sum().toInt()
        "${sum}%".also { binding.tvPercentageNutrition.text = it }
        if (sum == 100) {
            binding.tvPercentageNutrition.setTextColor(ContextCompat.getColor(this, R.color.green))
        } else {
            binding.tvPercentageNutrition.setTextColor(ContextCompat.getColor(this, R.color.red))
        }
    }

    override fun onValueChange(p0: NumberPicker?, p1: Int, p2: Int) {
        calculateValues(binding.etCaloriesNutrition.text.toString())
    }
}