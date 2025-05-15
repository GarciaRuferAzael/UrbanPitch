package com.example.urbanpitch.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {
    var isAuthenticated by mutableStateOf(false)
        private set

    fun login(email: String, password: String) {
        // real auth logic
        isAuthenticated = email.isNotEmpty() && password.isNotEmpty()
    }

    fun logout() {
        isAuthenticated = false
    }
}
