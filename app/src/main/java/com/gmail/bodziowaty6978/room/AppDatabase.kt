package com.gmail.bodziowaty6978.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gmail.bodziowaty6978.model.LogEntity
import com.gmail.bodziowaty6978.model.WeightEntity
import com.gmail.bodziowaty6978.room.dao.LogDao
import com.gmail.bodziowaty6978.room.dao.MeasurementDao
import com.gmail.bodziowaty6978.room.dao.WeightDao

@Database(entities = [LogEntity::class,WeightEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun logDao():LogDao
    abstract fun weightDao():WeightDao
    abstract fun measurementDao():MeasurementDao
}