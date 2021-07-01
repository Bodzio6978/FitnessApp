package com.gmail.bodziowaty6978.authActivities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.gmail.bodziowaty6978.AnimationHolder
import com.gmail.bodziowaty6978.MainActivity
import com.gmail.bodziowaty6978.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    lateinit var notification: TextView

    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var register: TextView
    lateinit var forgot: TextView
    lateinit var login: Button
    lateinit var help: TextView

    lateinit var instance: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        register = findViewById(R.id.login_register)
        notification = findViewById(R.id.login_notification)
        email = findViewById(R.id.login_email)
        password = findViewById(R.id.login_password)
        forgot = findViewById(R.id.login_forgot)
        login = findViewById(R.id.login_button)
        help = findViewById(R.id.login_help)

        instance = FirebaseAuth.getInstance()

        AnimationHolder.setAnimationContext(this)
        AnimationHolder.setUpAnimation()
        AnimationHolder.set.setTarget(notification)

        forgot.setOnClickListener {
            val intent = Intent(this, ForgotActivity::class.java)
            startActivity(intent)
        }

        help.setOnClickListener {
            notification.text = getString(R.string.this_will_be_implemented_in_the_future)
            AnimationHolder.set.start()
        }

        login.setOnClickListener {
            loginUser()
        }

        register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser() {

        val emailS: String = email.text.toString().trim()
        val passwordS: String = password.text.toString().trim()

        if (emailS.isEmpty() || passwordS.isEmpty()) {
            notification.text = getString(R.string.please_make_sure_all_fields_are_filled_in_correctly)
            AnimationHolder.set.start()
        } else {
            instance.signInWithEmailAndPassword(emailS, passwordS).addOnFailureListener {
                notification.text = it.message
                AnimationHolder.set.start()
            }.addOnCompleteListener {
                if (it.isSuccessful) {
                    val intent: Intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }


}