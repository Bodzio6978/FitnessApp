package com.gmail.bodziowaty6978.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityRegisterBinding
import com.gmail.bodziowaty6978.viewmodel.RegisterViewModel
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class RegisterActivity : AppCompatActivity(), LifecycleOwner {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        viewModel.getState().observe(this, {
            if(it){
                val intent = Intent(this, UsernameActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
        binding.btRegister.setOnClickListener {
            viewModel.registerUser(binding.etEmailRegister.text.toString(), binding.etPasswordRegister.text.toString(), binding.etConfirmRegister.text.toString())
        }

        binding.tvLoginRegister.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}