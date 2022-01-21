package com.gmail.bodziowaty6978.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gmail.bodziowaty6978.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        checkUserInformation()
    }

    private fun checkUserInformation() {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
//        UserInformation.getUserId()
//
//        UserInformation.mInformationState.observe(this, {
//            when (it.value) {
//                UserInformationState.USER_NOT_LOGGED -> {
//                    startActivity(Intent(this, LoginActivity::class.java))
//                    finish()
//                }
//
//                UserInformationState.USER_LOGGED -> {
//
//                    UserInformation.getValues()
//
//                }
//
//                UserInformationState.USER_INFORMATION_REQUIRED -> UserInformation.checkUser()
//
//                UserInformationState.USER_NO_USERNAME -> {
//                    startActivity(Intent(this, UsernameActivity::class.java))
//                    finish()
//                }
//
//                UserInformationState.USER_NO_INFORMATION -> {
//                    startActivity(Intent(this, IntroductionActivity::class.java))
//                    finish()
//                }
//
//                UserInformationState.USER_HAS_EVERYTHING -> {
//                    if (UserInformation.getUser().value != null) {
//                        startActivity(Intent(this, MainActivity::class.java))
//                        finish()
//                    }
//                }
//            }
//
//
//        })
    }
}