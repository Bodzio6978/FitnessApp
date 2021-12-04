package com.gmail.bodziowaty6978.view.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityUsernameBinding
import com.gmail.bodziowaty6978.view.MainActivity
import com.gmail.bodziowaty6978.viewmodel.auth.UsernameViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.DelicateCoroutinesApi


class UsernameActivity : AppCompatActivity(), LifecycleOwner {

    private lateinit var binding: ActivityUsernameBinding
    private lateinit var viewModel: UsernameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_username)

        binding = ActivityUsernameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(UsernameViewModel::class.java)
        viewModel.getState().observe(this, {
            if (it){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        viewModel.getSnackbarText().observe(this,{
            Snackbar.make(binding.clUsername,it, Snackbar.LENGTH_LONG).show()
        })

        binding.btUsername.setOnClickListener {
            addUsername()

        }
    }

    private fun addUsername(){
        val username = binding.etUsername.text.toString().trim()

        if (username.isEmpty()) {
            Snackbar.make(binding.clUsername,R.string.please_enter_your_username, Snackbar.LENGTH_LONG).show()
        }else if (username.length < 6 || username.length > 24) {
            Snackbar.make(binding.clUsername,R.string.username_length_notification, Snackbar.LENGTH_LONG).show()
        }else{
            viewModel.addUsername(username)
        }


    }
}