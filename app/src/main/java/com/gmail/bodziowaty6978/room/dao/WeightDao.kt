package com.gmail.bodziowaty6978.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gmail.bodziowaty6978.model.WeightEntity

@Dao
interface WeightDao {

    @Query("SELECT * FROM weight_table AS w ORDER BY w.time DESC LIMIT 20")
    suspend fun readLastWeightEntries():List<WeightEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addWeightEntry(entry:WeightEntity)


}