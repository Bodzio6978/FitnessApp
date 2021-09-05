package com.gmail.bodziowaty6978.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gmail.bodziowaty6978.R
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

//    private lateinit var binding: ActivityRegisterBinding

    lateinit var instance: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

//        binding = ActivityRegisterBinding.inflate(layoutInflater)
//        setContentView(binding.root)

        instance = FirebaseAuth.getInstance()


    }

//    private fun registerUser(){
//        val emailS = email.text.toString().trim()
//        val passwordS = password.text.toString().trim()
//        val confirmS = confirm.text.toString().trim()
//
//        if(emailS.isEmpty()||passwordS.isEmpty()||confirmS.isEmpty()){
//            notification.text = getString(R.string.please_make_sure_all_fields_are_filled_in_correctly)
//        }else if(passwordS != confirmS){
//            notification.text = getString(R.string.please_make_sure_both_passwords_are_the_same)
//        } else{
//            instance.createUserWithEmailAndPassword(emailS, passwordS).addOnFailureListener {
//                notification.text = it.message
//            }.addOnCompleteListener {
//                if(it.isSuccessful){
//                    val intent = Intent(this, UsernameActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                }
//
//            }
//        }
//    }
}