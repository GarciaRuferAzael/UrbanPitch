package com.example.urbanpitch.data.database

data class Pitch(
    val id: String = "",
    var name: String = "",
    var description: String = "",
    var city: String = "",
    var imageUrl: String = "",
    var longitude: Float = 0f,
    var latitude: Float = 0f
)

data class User(
    val id: String = "",
    var username:String = "",
    var email: String = "",
    var hashedPwd: String = "",
    var profileImageUri: String = ""
)

data class PitchFilter(
    val city: String? = null,
    val maxDistanceKm: Double? = null,
)

