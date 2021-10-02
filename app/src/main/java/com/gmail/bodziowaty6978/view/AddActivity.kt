package com.gmail.bodziowaty6978.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityAddBinding

class AddActivity : AppCompatActivity() {

    lateinit var binding:ActivityAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ibAdd.setOnClickListener {
            val intent = Intent(this, NewActivity::class.java)
            startActivity(intent)
        }

    }
}