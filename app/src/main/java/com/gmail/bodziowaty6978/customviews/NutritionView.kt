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

    fun setWanted(value: Int?){
        binding.tvWantedValueNutrition.text = value.toString()
    }

    fun updateValue(value:Int){
        binding.tvCurrentValueNutrition.text = value.toString()
        updateProgress()
    }

    fun updateProgress(){
        val current = binding.tvCurrentValueNutrition.text.toString().toDouble()
        val wanted = binding.tvWantedValueNutrition.text.toString().toDouble()

        binding.pbProgressNutrition.progress = (current/wanted*100).toInt()
    }
}