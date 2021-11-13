package com.gmail.bodziowaty6978.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.model.Meal
import com.gmail.bodziowaty6978.singleton.NotificationText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AddViewModel:ViewModel() {
    private val database = Firebase.database("https://fitness-app-fa608-default-rtdb.europe-west1.firebasedatabase.app/")
    private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

    private val searchResult = MutableLiveData<ArrayList<Meal>>()


    fun search(text: String){
        val mealRef = database.reference.child("meals")
        val query = mealRef.orderByChild("name").startAt(text).endAt("$text\uf8ff").limitToFirst(20)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value!=null){
                    val snapshotMap = snapshot.value as HashMap<*, *>
                    val keys = snapshotMap.keys

                    val mealList = ArrayList<Meal>()

                    for (key in keys) {
                        val mealMap = snapshotMap[key] as HashMap<*,*>
                        val meal = Meal(mealMap["author"] as String,
                                mealMap["name"] as String,
                                mealMap["brand"] as String,
                                mealMap["weight"] as String,
                                (mealMap["position"] as Long).toInt(),
                                mealMap["calories"] as String,
                                mealMap["carbs"] as String,
                                mealMap["protein"] as String,
                                mealMap["fat"] as String)

                        mealList.add(meal)

                    }
                    searchResult.value = mealList
                }else{
                    NotificationText.setText("There Are No Products With That Name")
                    NotificationText.startAnimation()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AddViewModel",error.message)
            }
        })

    }

    fun getSearchResult():MutableLiveData<ArrayList<Meal>> = searchResult


}