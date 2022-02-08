package com.gmail.bodziowaty6978.repository

import com.gmail.bodziowaty6978.model.LogEntry
import com.gmail.bodziowaty6978.model.WeightEntry
import com.gmail.bodziowaty6978.state.DataState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class MainRepository {
    private val db = Firebase.firestore
    private var userId = FirebaseAuth.getInstance().currentUser!!.uid
    private val userDocument = db.collection("users").document(userId)
    private val journalCollection = userDocument.collection("journal")
    private val weightCollection = userDocument.collection("weight")
    private val logCollection = userDocument.collection("logEntries")

    fun userId() = userId

    suspend fun getJournalEntries(date: String): List<DocumentSnapshot> {
        return try {
            journalCollection.whereEqualTo("date", date)
                .orderBy("time", Query.Direction.DESCENDING)
                .get().await().documents
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getWeightEntries(): MutableList<WeightEntry> {
        return try {
            weightCollection.limit(14).orderBy("time", Query.Direction.DESCENDING)
                .get().await().toObjects(WeightEntry::class.java)
        } catch (e: Exception) {
            emptyList<WeightEntry>().toMutableList()
        }
    }

    suspend fun getLastLogEntry():MutableList<LogEntry>{
        return try {
            logCollection.limit(1)
                .orderBy("time", Query.Direction.DESCENDING).get().await().toObjects(LogEntry::class.java)
        }catch (e:Exception){
            emptyList<LogEntry>().toMutableList()
        }
    }

    suspend fun addLogEntry(entry : LogEntry):DataState{
        return try {
            logCollection.add(entry).await()
            DataState.Success
        }catch (e:Exception){
            DataState.Error("An error has occurred when adding new log entry")
        }
    }

    suspend fun addWeightEntry(entry: WeightEntry): DataState {
        return try {
            db.collection("users").document(userId).collection("weight").add(
                entry
            ).await()
            DataState.Success
        } catch (e: Exception) {
            DataState.Error("An error has occurred when adding new weight entry")
        }
    }

    suspend fun setWeightDialogPermission(isAllowed: Boolean): DataState {
        return try {
            db.collection("users").document(userId).set(
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
            db.collection("users").document(userId)
                .collection("journal")
                .document(key).delete()
                .await()
            DataState.Success

        } catch (e: Exception) {
            DataState.Error("An error has occurred when removing a journal entry")
        }
    }
}