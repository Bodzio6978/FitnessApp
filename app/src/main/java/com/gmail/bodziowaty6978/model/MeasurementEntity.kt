package com.gmail.bodziowaty6978.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "measurement_table")
data class MeasurementEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "time") val time: Long = 0,
    @ColumnInfo(name = "date") val date: String = "",
    @ColumnInfo(name = "hips") val hips: Double = 0.0,
    @ColumnInfo(name = "waist") val waist: Double = 0.0,
    @ColumnInfo(name = "thigh") val thigh: Double = 0.0,
    @ColumnInfo(name = "bust") val bust: Double = 0.0,
    @ColumnInfo(name = "biceps") val biceps: Double = 0.0,
    @ColumnInfo(name = "calf") val calf: Double = 0.0
)
