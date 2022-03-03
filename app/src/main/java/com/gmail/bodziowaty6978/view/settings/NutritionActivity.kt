package com.gmail.bodziowaty6978.view.settings

import android.os.Bundle
import android.widget.EditText
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityNutritionBinding

class NutritionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNutritionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutrition)

        binding = ActivityNutritionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpNumberPickers(
            formatToString = { "$it%" }
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
            this.value = 50

            (NumberPicker::class.java.getDeclaredField("mInputText").apply { isAccessible = true }
                .get(this) as EditText).filters = emptyArray()
        }

        binding.npProteinNutrition.apply {
            setFormatter { formatToString(it) }
            wrapSelectorWheel = false

            minValue = (5)
            maxValue = (90)
            this.value = 50

            (NumberPicker::class.java.getDeclaredField("mInputText").apply { isAccessible = true }
                .get(this) as EditText).filters = emptyArray()
        }

        binding.npFatNutrition.apply {
            setFormatter { formatToString(it) }
            wrapSelectorWheel = false

            minValue = (5)
            maxValue = (90)
            this.value = 50

            (NumberPicker::class.java.getDeclaredField("mInputText").apply { isAccessible = true }
                .get(this) as EditText).filters = emptyArray()
        }


    }
}