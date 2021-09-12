package com.gmail.bodziowaty6978.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityLoginBinding
import com.gmail.bodziowaty6978.singleton.NotificationText
import com.gmail.bodziowaty6978.viewmodel.LoginViewModel
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class LoginActivity : AppCompatActivity(), LifecycleOwner {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel:LoginViewModel

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

        NotificationText.text.observe(this, { text ->
            changeNotificationText(text)
        })

        binding.tvForgotLogin.setOnClickListener {
            val intent = Intent(this, ForgotActivity::class.java)
            startActivity(intent)
        }
        binding.tvHelp.setOnClickListener {
            binding.nfvLogin.setText(getString(R.string.this_will_be_implemented_in_the_future))
            binding.nfvLogin.startAnimation()
        }

        binding.btnSignInLogin.setOnClickListener {
            val email = binding.etEmailLogin.text.toString().trim()
            val password = binding.etPasswordLogin.text.toString().trim()
            viewModel.loginUser(email, password)
        }

        binding.tvRegisterLogin.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun changeNotificationText(text:String){
        if (text == "fields"){
            binding.nfvLogin.apply {
                setText(getString(R.string.please_make_sure_all_fields_are_filled_in_correctly))
                startAnimation()
            }
        }else{
            binding.nfvLogin.apply {
                setText(text)
                startAnimation()
            }

        }
    }

}