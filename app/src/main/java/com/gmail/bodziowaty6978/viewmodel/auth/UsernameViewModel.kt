package com.gmail.bodziowaty6978.viewmodel.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.singleton.NotificationText
import com.gmail.bodziowaty6978.singleton.Strings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UsernameViewModel : ViewModel() {

    private val database = Firebase.database("https://fitness-app-fa608-default-rtdb.europe-west1.firebasedatabase.app/")
    private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val isUsernameSet: MutableLiveData<Boolean> = MutableLiveData(false)

    fun addUsername(username: String) {
        if (username.isEmpty()) {
            NotificationText.setText(Strings.get(R.string.please_enter_your_username))
            NotificationText.startAnimation()
        } else if (username.length < 6 || username.length > 24) {
            NotificationText.setText(Strings.get(R.string.username_length_notification))
            NotificationText.startAnimation()
        } else{
            checkIfUserExists(username)
        }

    }

    private fun checkIfUserExists(username: String){
        val refUsers = database.reference.child("users").orderByChild("username").equalTo(username)

        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var doesExist = true
                if(snapshot.value==null){
                    doesExist=false
                }
                if (doesExist) {
                    NotificationText.setText(Strings.get(R.string.username_exists_notification))
                    NotificationText.startAnimation()
                } else {
                    database.reference.child("users").child(userId).child("username").setValue(username).addOnSuccessListener {
                        isUsernameSet.value = true
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UsernameViewModel",error.message)
            }
        })

    }
    fun getState():MutableLiveData<Boolean> = isUsernameSet


}