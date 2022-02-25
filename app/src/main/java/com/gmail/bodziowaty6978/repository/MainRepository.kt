package com.gmail.bodziowaty6978.repository

import com.gmail.bodziowaty6978.model.LogEntity
import com.gmail.bodziowaty6978.model.WeightEntity
import com.gmail.bodziowaty6978.room.AppDatabase
import com.gmail.bodziowaty6978.state.DataState
import com.gmail.bodziowaty6978.state.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await


class MainRepository(roomDatabase: AppDatabase,
firestore:FirebaseFirestore) {

    private val firestore = Firebase.firestore

    private val logDao = roomDatabase.logDao()
    private val weightDao = roomDatabase.weightDao()

    private var userId = FirebaseAuth.getInstance().currentUser!!.uid
    private val userDocument = firestore.collection("users").document(userId)
    private val journalCollection = userDocument.collection("journal")

    fun userId() = userId

    suspend fun getJournalEntries(date: String): Resource<List<DocumentSnapshot>> {
        return try {
            Resource.Success(journalCollection.whereEqualTo("date", date)
                .orderBy("time", Query.Direction.DESCENDING)
                .get().await().documents)
        } catch (e: Exception) {
            Resource.Error("Error occurred when trying to download journal entries")
        }
    }

    suspend fun getWeightEntries(): MutableList<WeightEntity> {
        return try {
            weightDao.readLastWeightEntries().toMutableList()
        } catch (e: Exception) {
            emptyList<WeightEntity>().toMutableList()
        }
    }

    suspend fun getLastLogEntry():MutableList<LogEntity>{
        return try {
            logDao.readLastLog().toMutableList()
        }catch (e:Exception){
            emptyList<LogEntity>().toMutableList()
        }
    }

    suspend fun addLogEntry(entry : LogEntity):DataState{
        return try {
            logDao.addLogEntry(entry)
            DataState.Success
        }catch (e:Exception){
            DataState.Error("An error has occurred when adding new log entry")
        }
    }

    suspend fun addWeightEntry(entry: WeightEntity): DataState {
        return try {
            weightDao.addWeightEntry(entry)
            DataState.Success
        } catch (e: Exception) {
            DataState.Error("An error has occurred when adding new weight entry: ${e.message}")
        }
    }

    suspend fun setWeightDialogPermission(isAllowed: Boolean): DataState {
        return try {
            firestore.collection("users").document(userId).set(
                mapOf("areWeightDialogsEnabled" to isAllowed),
                SetOptions.merge()
            ).await()
            DataState.Success
        } catch (e: Exception) {
            DataState.Error("An error has occurred when setting permission for weight dialogs")
        }
    }

    suspend fun removeJournalEntry(key: String): DataState {
        return try {
            firestore.collection("users").document(userId)
                .collection("journal")
                .document(key).delete()
                .await()
            DataState.Success

        } catch (e: Exception) {
            DataState.Error("An error has occurred when removing a journal entry")
        }
    }
}