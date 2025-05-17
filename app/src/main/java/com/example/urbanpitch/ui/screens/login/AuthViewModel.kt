package com.example.urbanpitch.ui.screens.login

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.urbanpitch.data.database.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val _loginResult = MutableStateFlow<Result<Boolean>>(Result.success(false))
    val loginResult: StateFlow<Result<Boolean>> = _loginResult

    private val _isAuthenticated = mutableStateOf(false)
    val isAuthenticated: Boolean
        get() = auth.currentUser != null

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    init {
        fetchCurrentUser()
    }

    private fun fetchCurrentUser() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                document.toObject(User::class.java)?.let {
                    _currentUser.value = it
                }
            }
            .addOnFailureListener {
                Log.e("AuthViewModel", "Errore caricamento profilo: ${it.message}")
            }
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
    }

    fun register(username: String, email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val hashedPassword = hashPassword(password)

                    val newUser = User(
                        id = userId,
                        username = username,
                        email = email,
                        hashedPwd = hashedPassword,
                        profileImageUri = ""
                    )

                    db.collection("users").document(userId).set(newUser)
                        .addOnSuccessListener {
                            _isAuthenticated.value = true
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            onError("Errore durante il salvataggio su Firestore: ${e.localizedMessage}")
                        }
                } else {
                    onError("Errore durante la registrazione: ${task.exception?.localizedMessage}")
                }
            }
    }

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _loginResult.value = Result.success(true)
            }
            .addOnFailureListener { e ->
                _loginResult.value = Result.failure(e)
            }
    }

    private fun hashPassword(password: String): String {
        return password.hashCode().toString()
    }
}
