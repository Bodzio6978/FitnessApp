package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

@DelicateCoroutinesApi
class SplashViewModel: ViewModel() {
    private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val instance = FirebaseAuth.getInstance()
    private val hasUsername = MutableLiveData<Boolean>(true)
    private val isLoggedIn = MutableLiveData<Boolean>(true)
    private val isChecked = MutableLiveData<Boolean>(false)
    private val database = Firebase.database("https://fitness-app-fa608-default-rtdb.europe-west1.firebasedatabase.app/")

    fun checkIfUserIsLogged(){
        if(instance.currentUser==null){
            isLoggedIn.value = false
        }else{
            val ref = database.getReference("users").child(userId).child("username")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    GlobalScope.launch(Dispatchers.Main) {
                        delay(1000L)
                        if (snapshot.value == null) {
                            hasUsername.value = false
                        }else{
                            isChecked.value = true
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
            )
        }
    }
    fun isLogged():MutableLiveData<Boolean> = isLoggedIn
    fun hasUsername():MutableLiveData<Boolean> = hasUsername
    fun isChecked():MutableLiveData<Boolean> = isChecked

}