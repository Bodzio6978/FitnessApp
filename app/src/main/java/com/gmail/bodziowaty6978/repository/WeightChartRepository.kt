package com.gmail.bodziowaty6978.repository

import com.gmail.bodziowaty6978.model.WeightEntity
import com.gmail.bodziowaty6978.room.AppDatabase
import com.gmail.bodziowaty6978.state.Resource

class WeightChartRepository(roomDatabase: AppDatabase) {
    private val weightDao = roomDatabase.weightDao()

    suspend fun getAllWeightEntries():Resource<List<WeightEntity>>{
        return try {
            Resource.Success(weightDao.readAllWeightEntries())
        }catch (e:Exception){
            Resource.Error(e.message.toString())
        }
    }
}