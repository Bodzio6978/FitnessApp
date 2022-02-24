package com.gmail.bodziowaty6978.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "log_table")
data class LogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "time") val time:Long,
    @ColumnInfo(name = "strike") val strike:Int
)
