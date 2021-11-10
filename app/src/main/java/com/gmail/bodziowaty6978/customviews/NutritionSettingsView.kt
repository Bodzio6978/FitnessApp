package com.gmail.bodziowaty6978.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.NutritionSettingsViewBinding

class NutritionSettingsView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    var binding = NutritionSettingsViewBinding.inflate(LayoutInflater.from(context))

    var calories = 0

    init {
        addView(binding.root)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.NutritionSettingsView)

        binding.tvNameSettingsView.text =
            attributes.getString(R.styleable.NutritionSettingsView_nutritionName)

        binding.tvSecondSettingsView.text = context.getString(R.string.percentage)

        attributes.recycle()
    }

    private fun setCaloriesValue(value:Int){
        this.calories = value
    }

}