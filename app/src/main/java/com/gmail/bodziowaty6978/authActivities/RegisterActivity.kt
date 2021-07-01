package com.gmail.bodziowaty6978.authActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.gmail.bodziowaty6978.AnimationHolder
import com.gmail.bodziowaty6978.R
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    lateinit var notification: TextView

    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var confirm: EditText
    lateinit var login: TextView
    lateinit var register: Button
    lateinit var help: TextView

    lateinit var instance: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        notification = findViewById(R.id.register_notification)

        AnimationHolder.setUpAnimation()
        AnimationHolder.setAnimationContext(this)
        AnimationHolder.set.setTarget(notification)

        email = findViewById(R.id.register_email)
        password = findViewById(R.id.register_password)
        confirm = findViewById(R.id.register_confirm)
        login = findViewById(R.id.register_login)
        help = findViewById(R.id.register_help)
        register = findViewById(R.id.register_button)

        instance = FirebaseAuth.getInstance()

        help.setOnClickListener {
            notification.text = getString(R.string.this_will_be_implemented_in_the_future)
            AnimationHolder.set.start()
        }

        login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        register.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser(){
        val emailS = email.text.toString().trim()
        val passwordS = password.text.toString().trim()
        val confirmS = confirm.text.toString().trim()

        if(emailS.isEmpty()||passwordS.isEmpty()||confirmS.isEmpty()){
            notification.text = getString(R.string.please_make_sure_all_fields_are_filled_in_correctly)
            AnimationHolder.set.start()
        }else if(passwordS != confirmS){
            notification.text = getString(R.string.please_make_sure_both_passwords_are_the_same)
            AnimationHolder.set.start()
        } else{
            instance.createUserWithEmailAndPassword(emailS, passwordS).addOnFailureListener {
                notification.text = it.message
                AnimationHolder.set.start()
            }.addOnCompleteListener {
                if(it.isSuccessful){
                    val intent = Intent(this, UsernameActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }
        }
    }
}