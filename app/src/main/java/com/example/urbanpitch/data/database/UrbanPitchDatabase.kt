package com.example.urbanpitch.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Pitch::class, User::class], version = 3)
abstract class UrbanPitchDatabase : RoomDatabase() {
    abstract fun pitchesDAO(): PitchesDAO
    abstract fun userDAO(): UserDAO
}