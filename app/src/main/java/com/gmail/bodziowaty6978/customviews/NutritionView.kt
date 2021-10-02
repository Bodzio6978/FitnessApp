package com.gmail.bodziowaty6978.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.NutritionViewBinding

class NutritionView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    var binding: NutritionViewBinding = NutritionViewBinding.inflate(LayoutInflater.from(context))

    init {
        addView(binding.root)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.NutritionView)

        binding.tvNameNutrition.text = attributes.getString(R.styleable.NutritionView_name)
        binding.tvCurrentValueNutrition.text = attributes.getInteger(R.styleable.NutritionView_currentValue, 0).toString()
        binding.tvWantedValueNutrition.text = attributes.getInteger(R.styleable.NutritionView_wantedValue, 0).toString()
        binding.pbProgressNutrition.progress = attributes.getInteger(R.styleable.NutritionView_progressState,0)
        
        attributes.recycle()
    }

    fun refreshProgress(){

    }
}