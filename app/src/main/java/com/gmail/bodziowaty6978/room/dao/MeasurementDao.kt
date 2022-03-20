package com.gmail.bodziowaty6978.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gmail.bodziowaty6978.model.MeasurementEntity

@Dao
interface MeasurementDao {

    @Query("SELECT * FROM measurement_table AS l ORDER BY l.time DESC LIMIT 2")
    suspend fun readLastMeasurementEntities():List<MeasurementEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMeasurementEntry(entry: MeasurementEntity)
}