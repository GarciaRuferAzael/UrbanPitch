package com.example.urbanpitch.data.repositories

import com.example.urbanpitch.data.database.User
import com.example.urbanpitch.data.database.UserDAO

class UsersRepository(private val userDao: UserDAO) {
    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }
}
