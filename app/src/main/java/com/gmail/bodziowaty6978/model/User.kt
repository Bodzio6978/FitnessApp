package com.gmail.bodziowaty6978.model

data class User(
        var userId:String? = null,
        var username: String? = null,
        var nutritionValues: Map<String, Double>? = null,
        var userInformation: Map<String, String>? = null
)
