package com.gmail.bodziowaty6978.repository

import com.gmail.bodziowaty6978.model.LogEntity
import com.gmail.bodziowaty6978.model.MeasurementEntity
import com.gmail.bodziowaty6978.model.WeightEntity
import com.gmail.bodziowaty6978.room.AppDatabase
import com.gmail.bodziowaty6978.state.DataState
import com.gmail.bodziowaty6978.state.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await


class MainRepository(
    roomDatabase: AppDatabase,
    val firestore: FirebaseFirestore
) {

    private val logDao = roomDatabase.logDao()
    private val weightDao = roomDatabase.weightDao()
    private val measurementDao = roomDatabase.measurementDao()

    private var userId = FirebaseAuth.getInstance().currentUser?.uid
    private val userCollection = firestore.collection("users")

    fun userId() = userId

    suspend fun getJournalEntries(date: String): Resource<List<DocumentSnapshot>> {
        return try {
            Resource.Success(
                userCollection.document(userId!!).collection("journal").whereEqualTo("date", date)
                    .orderBy("time", Query.Direction.DESCENDING)
                    .get().await().documents
            )
        } catch (e: Exception) {
            Resource.Error("Error occurred when trying to download journal entries")
        }
    }

    suspend fun getMeasurementEntries(): MutableList<MeasurementEntity> {
        return try {
            measurementDao.readLastMeasurementEntities().toMutableList()
        } catch (e: Exception) {
            emptyList<MeasurementEntity>().toMutableList()
        }
    }

    suspend fun getWeightEntries(): MutableList<WeightEntity> {
        return try {
            weightDao.readLastWeightEntries().toMutableList()
        } catch (e: Exception) {
            emptyList<WeightEntity>().toMutableList()
        }
    }

    suspend fun getLastLogEntry(): MutableList<LogEntity> {
        return try {
            logDao.readLastLog().toMutableList()
        } catch (e: Exception) {
            emptyList<LogEntity>().toMutableList()
        }
    }

    suspend fun addLogEntry(entry: LogEntity): DataState {
        return try {
            logDao.addLogEntry(entry)
            DataState.Success
        } catch (e: Exception) {
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
            firestore.collection("users").document(userId!!).set(
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
            firestore.collection("users").document(userId!!)
                .collection("journal")
                .document(key).delete()
                .await()
            DataState.Success

        } catch (e: Exception) {
            DataState.Error("An error has occurred when removing a journal entry")
        }
    }
}