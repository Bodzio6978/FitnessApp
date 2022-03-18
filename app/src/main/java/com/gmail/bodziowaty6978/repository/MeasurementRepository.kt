package com.gmail.bodziowaty6978.repository

import com.gmail.bodziowaty6978.model.MeasurementEntity
import com.gmail.bodziowaty6978.room.AppDatabase
import com.gmail.bodziowaty6978.state.DataState

class MeasurementRepository(val roomDatabase: AppDatabase) {

    private val measurementDao = roomDatabase.measurementDao()


    suspend fun addMeasurementEntity(measurementEntity: MeasurementEntity):DataState{
        return try {
            measurementDao.addMeasurementEntry(measurementEntity)
            DataState.Success
        }catch (e:Exception){
            DataState.Error(e.message.toString())
        }
    }
}