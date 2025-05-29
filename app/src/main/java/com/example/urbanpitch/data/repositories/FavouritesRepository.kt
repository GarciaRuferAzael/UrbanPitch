package com.example.urbanpitch.data.repositories

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FavoritesRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getFavorites(userId: String): Flow<List<String>> = callbackFlow {
        val ref = db.collection("users").document(userId).collection("favorites")
        val listener = ref.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }

            val favoriteIds = snapshot?.documents?.map { it.id } ?: emptyList()
            trySend(favoriteIds)
        }
        awaitClose { listener.remove() }
    }

    suspend fun addFavorite(userId: String, pitchId: String) {
        db.collection("users").document(userId)
            .collection("favorites").document(pitchId).set(mapOf("addedAt" to FieldValue.serverTimestamp()))
    }

    suspend fun removeFavorite(userId: String, pitchId: String) {
        db.collection("users").document(userId)
            .collection("favorites").document(pitchId).delete()
    }

    suspend fun isFavorite(userId: String, pitchId: String): Boolean {
        val doc = db.collection("users").document(userId)
            .collection("favorites").document(pitchId).get().await()
        return doc.exists()
    }
}
