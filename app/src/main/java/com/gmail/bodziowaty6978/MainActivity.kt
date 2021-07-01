package com.gmail.bodziowaty6978

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gmail.bodziowaty6978.authActivities.LoginActivity
import com.gmail.bodziowaty6978.authActivities.UsernameActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    lateinit var database: FirebaseDatabase
    lateinit var instance: FirebaseAuth
    lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        instance = FirebaseAuth.getInstance()

        instance = FirebaseAuth.getInstance()
        database = Firebase.database("https://learn-germany-app-default-rtdb.europe-west1.firebasedatabase.app/")


        if (instance.currentUser == null) {
            val intent: Intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            userId = instance.uid.toString()

            val ref = database.getReference("users").child(userId).child("username")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null) {
                        val intent: Intent = Intent(this@MainActivity, UsernameActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
            )
        }
    }
}