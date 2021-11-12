package com.gmail.bodziowaty6978.viewmodel

import androidx.collection.ArrayMap
import androidx.collection.SimpleArrayMap
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.view.introduction.FirstFragment
import com.gmail.bodziowaty6978.view.introduction.SecondFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class IntroductionViewModel:ViewModel() {

    private val fragment1 = FirstFragment()
    private val fragment2 = SecondFragment()

    private val database = Firebase.database("https://fitness-app-fa608-default-rtdb.europe-west1.firebasedatabase.app/")
    private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

    private val userInformation = ArrayMap<String,String>()

    private val addingState = MutableLiveData<Boolean>(false)

    fun getFragments():ArrayList<Fragment>{
        val list = ArrayList<Fragment>()
        list.add(fragment1)
        list.add(fragment2)
        return list
    }

    fun clearData(){
        userInformation.clear()
    }

    fun addInformation(data: SimpleArrayMap<String, String>,isFinished:Boolean){
        userInformation.putAll(data)

        if(isFinished){
            val userRef = database.reference.child("users").child(userId).child("information")
            userRef.setValue(userInformation).addOnCompleteListener {
                if(it.isSuccessful){
                    addingState.value = true
                }
            }
        }
    }
}