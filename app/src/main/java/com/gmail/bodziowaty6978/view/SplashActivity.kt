package com.gmail.bodziowaty6978.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.singleton.InformationState
import com.gmail.bodziowaty6978.singleton.UserInformation
import com.gmail.bodziowaty6978.view.auth.LoginActivity
import com.gmail.bodziowaty6978.view.auth.UsernameActivity
import com.gmail.bodziowaty6978.view.introduction.IntroductionActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        checkUserInformation()
    }

    private fun checkUserInformation(){
        UserInformation.getUserId()

        UserInformation.mInformationState.observe(this,{
            when(it.value){
                InformationState.USER_NOT_LOGGED -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }

                InformationState.USER_LOGGED -> UserInformation.getValues()

                InformationState.USER_INFORMATION_REQUIRED -> UserInformation.checkUser()

                InformationState.USER_NO_USERNAME -> {
                    startActivity(Intent(this, UsernameActivity::class.java))
                    finish()
                }

                InformationState.USER_NO_INFORMATION -> {
                    startActivity(Intent(this, IntroductionActivity::class.java))
                    finish()
                }

                InformationState.USER_HAS_EVERYTHING -> {
                    if (UserInformation.getUser().value!=null){
                        startActivity(Intent(this,MainActivity::class.java))
                        finish() }
                    }
                }


        })
    }
}