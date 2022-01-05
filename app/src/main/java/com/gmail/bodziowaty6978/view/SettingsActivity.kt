package com.gmail.bodziowaty6978.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivitySettingsBinding
import com.gmail.bodziowaty6978.view.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsBinding

    private lateinit var instance:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        binding = ActivitySettingsBinding.inflate(layoutInflater)

        instance = FirebaseAuth.getInstance()

        binding.rlLogOutSettings.setOnClickListener {
            instance.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.rlGoalSettings.setOnClickListener {
            val intent = Intent(this, GoalActivity::class.java)
            startActivity(intent)
        }
    }
}