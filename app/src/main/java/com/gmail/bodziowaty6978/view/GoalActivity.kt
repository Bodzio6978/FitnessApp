package com.gmail.bodziowaty6978.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityGoalBinding
import com.gmail.bodziowaty6978.viewmodel.GoalViewModel

class GoalActivity : AppCompatActivity(), LifecycleOwner {
    private lateinit var binding: ActivityGoalBinding
    private lateinit var viewModel: GoalViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        binding = ActivityGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(GoalViewModel::class.java)

        binding.svCaloriesGoal.setOnClickListener {
            val intent = Intent(this, NutritionSettings::class.java)
            startActivity(intent)
        }



    }
}