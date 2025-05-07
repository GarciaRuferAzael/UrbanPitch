package com.example.urbanpitch.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PitchesDAO {
    @Query("SELECT * FROM pitch")
    fun getAll(): Flow<List<Pitch>>

    @Query("SELECT * FROM pitch WHERE city = :city ")
    fun getByCity(city: String): Flow<List<Pitch>>

    @Upsert
    suspend fun upsert(pitch: Pitch)

    @Delete
    suspend fun delete(item: Pitch)

}

@Dao
interface UserDAO {
    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?
}
