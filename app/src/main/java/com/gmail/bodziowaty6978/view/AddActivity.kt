package com.gmail.bodziowaty6978.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityAddBinding
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class AddActivity : AppCompatActivity() {

    lateinit var binding:ActivityAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mealName = intent.getStringExtra("mealName")
        binding.tvMealNameAdd.text = mealName

        binding.ibAdd.setOnClickListener {
            val intent = Intent(this, NewActivity::class.java).putExtra("mealName",mealName)
            startActivity(intent)
        }
        binding.ibBackAdd.setOnClickListener {
            super.onBackPressed()
        }

    }
}