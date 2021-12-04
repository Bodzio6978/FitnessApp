package com.gmail.bodziowaty6978.view.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityLoginBinding
import com.gmail.bodziowaty6978.view.MainActivity
import com.gmail.bodziowaty6978.viewmodel.auth.LoginViewModel
import com.google.android.material.snackbar.Snackbar


class LoginActivity : AppCompatActivity(), LifecycleOwner {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        viewModel.getState().observe(this, {
            if(it){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        viewModel.getSnackbarText().observe(this,{
            Snackbar.make(binding.clLogin,it, Snackbar.LENGTH_LONG).show()
        })

        binding.tvForgotLogin.setOnClickListener {
            val intent = Intent(this, ForgotActivity::class.java)
            startActivity(intent)
        }
        binding.tvHelp.setOnClickListener {
            Snackbar.make(binding.clLogin,R.string.this_will_be_implemented_in_the_future, Snackbar.LENGTH_LONG).show()
        }

        binding.btnSignInLogin.setOnClickListener {
            loginUser()

        }

        binding.tvRegisterLogin.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(){
        val email = binding.etEmailLogin.text.toString().trim()
        val password = binding.etPasswordLogin.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(binding.clLogin,R.string.please_make_sure_all_fields_are_filled_in_correctly, Snackbar.LENGTH_LONG).show()
        }else{
            viewModel.loginUser(email, password)
        }
    }
}