package com.gmail.bodziowaty6978.view.introduction

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.adapters.ViewPagerAdapter
import com.gmail.bodziowaty6978.databinding.ActivityIntroductionBinding
import com.gmail.bodziowaty6978.interfaces.OnClearDataRequest
import com.gmail.bodziowaty6978.interfaces.OnFragmentChangeRequest
import com.gmail.bodziowaty6978.interfaces.OnMapPassed
import com.gmail.bodziowaty6978.state.DataState
import com.gmail.bodziowaty6978.view.MainActivity
import com.gmail.bodziowaty6978.viewmodel.IntroductionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroductionActivity : AppCompatActivity(), OnMapPassed, OnFragmentChangeRequest, OnClearDataRequest, LifecycleOwner {

    lateinit var binding:ActivityIntroductionBinding
    val viewModel: IntroductionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction)

        binding = ActivityIntroductionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launchWhenStarted {
            viewModel.addingInformation.observe(this@IntroductionActivity,{
                when (it){
                    is DataState.Success -> {
                        val intent = Intent(this@IntroductionActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else -> {}
                }
            })
        }


        val adapter = ViewPagerAdapter(supportFragmentManager,lifecycle,viewModel.getFragments())
        binding.vp2Introduction.adapter = adapter
    }

    override fun onRequest(position: Int) {
        binding.vp2Introduction.currentItem = position
    }

    override fun onClearDataRequest() {
        viewModel.clearData()
    }

    override fun onDataPass(data: Map<String, String>, isFinished: Boolean) {
        if (data.isNotEmpty()) {
            viewModel.addInformation(data, isFinished)
        }
    }
}