package com.gmail.bodziowaty6978.functions

import com.gmail.bodziowaty6978.model.MeasurementEntity

fun MeasurementEntity.getMeasurementMap():Map<String,String> = mapOf(
    "hips" to this.hips.toString(),
    "waist" to this.waist.toString(),
    "thigh" to this.thigh.toString(),
    "bust" to this.bust.toString(),
    "biceps" to this.biceps.toString(),
    "calf" to this.calf.toString()
)