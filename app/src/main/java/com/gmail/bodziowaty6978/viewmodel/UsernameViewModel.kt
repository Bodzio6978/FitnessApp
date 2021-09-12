package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.singleton.NetworkCallState
import com.gmail.bodziowaty6978.singleton.NotificationText
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
            NotificationText.text.value = "username"
        } else if (username.length < 6 || username.length > 24) {
            NotificationText.text.value = "length"
        } else{
            checkIfUserExists(username)
        }

    }

    private fun checkIfUserExists(username: String){
        val refUsers = database.reference.child("users")

        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var doesExist = false
                for (data in snapshot.children) {
                    if (data.child("username").value.toString() == username) {
                        doesExist = true
                    }
                }
                if (doesExist) {
                    NotificationText.text.value = "exists"
                } else {
                    database.reference.child("users").child(userId).child("username").setValue(username).addOnSuccessListener {
                        isUsernameSet.value = true
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
                NetworkCallState.status.value = NetworkCallState.NETWORK_ERROR
            }
        })

    }
    fun getState():MutableLiveData<Boolean> = isUsernameSet


}