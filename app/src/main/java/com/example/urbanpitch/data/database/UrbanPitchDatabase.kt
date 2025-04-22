package com.example.urbanpitch.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Pitch::class], version = 2)
abstract class UrbanPitchDatabase : RoomDatabase() {
    abstract fun pitchesDAO(): PitchesDAO
}