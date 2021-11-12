package com.gmail.bodziowaty6978.view.introduction

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.ArrayMap
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.adapters.ViewPagerAdapter
import com.gmail.bodziowaty6978.databinding.ActivityIntroductionBinding
import com.gmail.bodziowaty6978.interfaces.OnClearDataRequest
import com.gmail.bodziowaty6978.interfaces.OnDataPassIntroduction
import com.gmail.bodziowaty6978.interfaces.OnRequestFragmentChange
import com.gmail.bodziowaty6978.view.MainActivity
import com.gmail.bodziowaty6978.viewmodel.IntroductionViewModel
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class IntroductionActivity : AppCompatActivity(), OnDataPassIntroduction, OnRequestFragmentChange, OnClearDataRequest, LifecycleOwner {

    lateinit var binding:ActivityIntroductionBinding
    lateinit var viewModel: IntroductionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction)

        binding = ActivityIntroductionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(IntroductionViewModel::class.java)

        viewModel.getAddingState().observe(this,{
            if (it){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        val adapter = ViewPagerAdapter(supportFragmentManager,lifecycle,viewModel.getFragments())
        binding.vp2Introduction.adapter = adapter
    }

    override fun onDataPass(data: ArrayMap<String, String>,isFinished:Boolean) {
        if (!data.isEmpty) {
            viewModel.addInformation(data, isFinished)
        }
    }

    override fun onRequest(position: Int) {
        binding.vp2Introduction.currentItem = position
    }

    override fun onClearDataRequest() {
        viewModel.clearData()
    }
}