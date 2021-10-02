package com.gmail.bodziowaty6978.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityNewBinding
import com.gmail.bodziowaty6978.viewmodel.NewViewModel

class NewActivity : AppCompatActivity(),LifecycleOwner {

    lateinit var viewModel: NewViewModel
    lateinit var binding:ActivityNewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)

        binding = ActivityNewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(NewViewModel::class.java)
    }
}