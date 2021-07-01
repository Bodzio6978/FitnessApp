package com.gmail.bodziowaty6978.authActivities

import android.animation.Animator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.gmail.bodziowaty6978.AnimationHolder
import com.gmail.bodziowaty6978.MainActivity
import com.gmail.bodziowaty6978.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UsernameActivity : AppCompatActivity() {

    lateinit var database : FirebaseDatabase
    lateinit var instance : FirebaseAuth
    lateinit var userId : String

    lateinit var username : EditText
    lateinit var setUsername : Button

    lateinit var notification : TextView
    lateinit var set : Animator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_username)

        notification = findViewById(R.id.username_notification)

        AnimationHolder.setUpAnimation()
        AnimationHolder.setAnimationContext(this)
        AnimationHolder.set.setTarget(notification)

        database = Firebase.database("https://fitness-app-fa608-default-rtdb.europe-west1.firebasedatabase.app/")
        instance = FirebaseAuth.getInstance()

        userId=instance.currentUser?.uid.toString()

        username = findViewById(R.id.username)
        setUsername = findViewById(R.id.username_button)




        setUsername.setOnClickListener {
            addUsername()
        }
    }

    private fun addUsername(){
        val username = username.text.toString().trim()
        if(username.isNullOrEmpty()){
            notification.text = getString(R.string.please_enter_your_username)
            AnimationHolder.set.start()
        }else if(username.length<6||username.length>24){
            notification.text = getString(R.string.username_length_notification)
            AnimationHolder.set.start()
        }else{
            checkIfUserExists()
        }
    }

    private fun checkIfUserExists(){
        val username = username.text.toString().trim()
        val refUsers = database.reference.child("users")

        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var doesExist = false
                for (data in snapshot.children) {
                    if (data.child("username").value.toString() == username) {
                        doesExist = true
                    }
                }
                if(doesExist){
                    notification.text = getString(R.string.username_exists_notification)
                    set.start()
                }else{
                    database.reference.child("users").child(userId).child("username").setValue(username)
                    val intent : Intent = Intent(this@UsernameActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
}