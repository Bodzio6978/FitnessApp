package com.gmail.bodziowaty6978.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityUsernameBinding
import com.gmail.bodziowaty6978.singleton.NetworkCallState
import com.gmail.bodziowaty6978.singleton.NotificationText
import com.gmail.bodziowaty6978.viewmodel.UsernameViewModel
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
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

        NotificationText.text.observe(this, {
            changeNotificationText(it)
        })

        NetworkCallState.status.observe(this, {
            if(it == 1){
                val intent = Intent(this, ErrorActivity::class.java)
                startActivity(intent)
            }
        })

        binding.btUsername.setOnClickListener {
            viewModel.addUsername(binding.etUsername.text.toString())
        }
    }



    private fun changeNotificationText(text:String){
        when (text) {
            "username" -> binding.nfvUsername.apply {
                setText(getString(R.string.please_enter_your_username))
                startAnimation()
            }
            "length" -> binding.nfvUsername.apply {
                setText(getString(R.string.username_length_notification))
                startAnimation()
            }
            "exists" -> binding.nfvUsername.apply {
                setText(getString(R.string.username_exists_notification))
                startAnimation()
            }
            else -> binding.nfvUsername.apply {
                setText(text)
                startAnimation()
            }
        }
    }


}