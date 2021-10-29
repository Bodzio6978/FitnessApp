package com.gmail.bodziowaty6978.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.SettingsViewBinding

class SettingsView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    var binding = SettingsViewBinding.inflate(LayoutInflater.from(context))

    init {
        addView(binding.root)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SettingsView)

        binding.ivImageSettingsView.setImageDrawable(attributes.getDrawable(R.styleable.SettingsView_image))
        binding.tvTextSettingsView.text = attributes.getString(R.styleable.SettingsView_settingText)

        attributes.recycle()
    }

}