package com.gmail.bodziowaty6978.view.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityRegisterBinding
import com.gmail.bodziowaty6978.state.UiState
import com.gmail.bodziowaty6978.view.MainActivity
import com.gmail.bodziowaty6978.viewmodel.auth.RegisterViewModel
import com.google.android.material.snackbar.Snackbar


class RegisterActivity : AppCompatActivity(), LifecycleOwner {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        viewModel.uiState().observe(this,{
            when(it){
                is UiState.Success -> onSuccess()
                is UiState.Loading -> onLoading()
                is UiState.Error -> onError(it)
            }
        })

        binding.btRegister.setOnClickListener {
            registerUser()
        }

        binding.tvLoginRegister.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onLoading(){
        binding.rlRegister.visibility = View.GONE
        binding.pbRegister.visibility = View.VISIBLE

    }

    private fun onError(state:UiState.Error){
        binding.rlRegister.visibility = View.VISIBLE
        binding.pbRegister.visibility = View.GONE
        Snackbar.make(binding.clRegister,state.errorMessage,Snackbar.LENGTH_LONG).show()
    }

    private fun onSuccess(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun registerUser(){
        val username = binding.etUsernameRegister.text.toString().trim()
        val email = binding.etEmailRegister.text.toString().trim()
        val password = binding.etPasswordRegister.text.toString().trim()
        val confirm = binding.etConfirmRegister.text.toString().trim()

        viewModel.registerUser(username,email,password,confirm)
    }
}