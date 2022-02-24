package com.gmail.bodziowaty6978.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gmail.bodziowaty6978.model.LogEntity

@Dao
interface LogDao {

    @Query("SELECT * FROM log_table AS l ORDER BY l.time DESC LIMIT 1")
    suspend fun readLastLog():List<LogEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addLogEntry(entry: LogEntity)
}