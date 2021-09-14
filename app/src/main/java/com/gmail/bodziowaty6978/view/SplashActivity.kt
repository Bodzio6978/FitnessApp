package com.gmail.bodziowaty6978.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivitySplashBinding
import com.gmail.bodziowaty6978.viewmodel.SplashViewModel
import kotlinx.coroutines.DelicateCoroutinesApi


@DelicateCoroutinesApi
class SplashActivity : AppCompatActivity(), LifecycleOwner {

    lateinit var binding: ActivitySplashBinding
    lateinit var viewModel: SplashViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)
        viewModel.checkIfUserIsLogged()
        viewModel.isLogged().observe(this,{
            if (!it){
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
        viewModel.hasUsername().observe(this,{
            if(!it){
                val intent = Intent(this, UsernameActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
        viewModel.isChecked().observe(this,{
            if (it){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })






    }
}