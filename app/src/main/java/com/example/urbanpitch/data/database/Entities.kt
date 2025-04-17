package com.example.urbanpitch.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity
data class Pitch (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo
    var name: String,

    @ColumnInfo
    var description: String,

    @ColumnInfo
    var city: String,

    @ColumnInfo
    var longitude: Float,

    @ColumnInfo
    var latitude: Float
)

@Entity
data class User (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo
    var username: String,

    @ColumnInfo
    var email: String,

    @ColumnInfo
    var hashed_password: String
)


@Entity(
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Pitch::class,
            parentColumns = ["id"],
            childColumns = ["pitch_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("user_id"), Index("pitch_id")]
)
data class Favourites (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "pitch_id")
    val pitchId: Int
)