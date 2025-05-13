package com.example.urbanpitch.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

data class Pitch(
    val id: String = "",  // Firebase usa String come id
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
    var hashed_password: String = "",
    var profileImageUri: String = ""
)