package com.gmail.bodziowaty6978.view.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityRegisterBinding
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
        viewModel.getState().observe(this, {
            if(it){
                val intent = Intent(this, UsernameActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        viewModel.getSnackbarText().observe(this,{
            Snackbar.make(binding.clRegister,it, Snackbar.LENGTH_LONG).show()
        })

        binding.btRegister.setOnClickListener {
            registerUser()
        }

        binding.tvLoginRegister.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun registerUser(){
        val email = binding.etEmailRegister.text.toString().trim()
        val password = binding.etPasswordRegister.text.toString().trim()
        val confirm = binding.etConfirmRegister.text.toString().trim()

        val snackbar = Snackbar.make(binding.clRegister,"",Snackbar.LENGTH_LONG)

        if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            snackbar.apply {
                setText(R.string.please_make_sure_all_fields_are_filled_in_correctly)
                show()
            }
        }else if(password != confirm){
            snackbar.apply {
                setText(R.string.please_make_sure_both_passwords_are_the_same)
                show()
            }
        }else if(password.length<6||password.length>24){
            snackbar.apply {
                setText(R.string.please_make_sure_your_password_is_at_least_6_characters_and_is_not_longer_than_24_characters)
                show()
            }
        }else{
            viewModel.registerUser(email,password)
        }

    }
}