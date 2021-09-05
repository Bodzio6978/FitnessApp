package com.gmail.bodziowaty6978.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityLoginBinding
import com.gmail.bodziowaty6978.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class LoginActivity : AppCompatActivity(), LifecycleOwner {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel:LoginViewModel

    lateinit var instance: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        viewModel.getNotificationText().observe(this, Observer<String> { text ->
            changeNotificationText(text)
        })

        instance = FirebaseAuth.getInstance()

        binding.tvForgot.setOnClickListener {
            val intent = Intent(this, ForgotActivity::class.java)
            startActivity(intent)
        }

        binding.tvHelp.setOnClickListener {
            binding.nfvNotification.setText(getString(R.string.this_will_be_implemented_in_the_future))
            binding.nfvNotification.startAnimation()
        }

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.loginUser(email, password)
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun changeNotificationText(text:String){
        if (text == "please"){
            binding.nfvNotification.apply {
                setText(getString(R.string.please_make_sure_all_fields_are_filled_in_correctly))
                startAnimation()
            }
        }else{
            binding.nfvNotification.apply {
                setText(text)
                startAnimation()
            }

        }
    }

}