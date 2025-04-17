package com.example.urbanpitch.data.repositories

import com.example.urbanpitch.data.database.Pitch
import com.example.urbanpitch.data.database.PitchesDao
import kotlinx.coroutines.flow.Flow

class PitchRepository (private val dao: PitchesDao) {
    val pitches: Flow<List<Pitch>> = dao.getAll()

    fun getByCity(city: String) = dao.getByCity(city)

    suspend fun upsert(pitch: Pitch) = dao.upsert(pitch)

    suspend fun delete(pitch: Pitch) = dao.delete(pitch)
}