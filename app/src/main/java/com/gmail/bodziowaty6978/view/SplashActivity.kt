package com.gmail.bodziowaty6978.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivitySplashBinding
import kotlinx.coroutines.*


@DelicateCoroutinesApi
class SplashActivity : AppCompatActivity(), LifecycleOwner {

    lateinit var binding: ActivitySplashBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = Intent(this, MainActivity::class.java)
        GlobalScope.launch(Dispatchers.Main){
            delay(1000L)
            startActivity(intent)
            finish()
        }
    }
}