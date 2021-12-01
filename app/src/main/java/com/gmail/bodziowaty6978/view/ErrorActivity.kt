package com.gmail.bodziowaty6978.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityErrorBinding

class ErrorActivity : AppCompatActivity() {

    lateinit var binding: ActivityErrorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)

        binding = ActivityErrorBinding.inflate(layoutInflater)

        (intent.getStringExtra("errorMessage").let { if(it!=null) binding.tvErrorMessage.text = it})

        setContentView(binding.root)

    }
}