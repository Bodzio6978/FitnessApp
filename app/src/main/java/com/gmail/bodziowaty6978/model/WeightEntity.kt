package com.gmail.bodziowaty6978.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_table")
data class WeightEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "time") val time: Long = 0,
    @ColumnInfo(name = "value") val value: Double = 0.0,
    @ColumnInfo(name = "date") val date: String = ""
)
