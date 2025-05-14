package com.example.urbanpitch.data.repositories

import com.example.urbanpitch.data.database.Pitch
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class PitchesRepositoryFirebase {

    private val db = FirebaseFirestore.getInstance()
    private val pitchesCollection = db.collection("pitches")

    fun getAll(): Flow<List<Pitch>> = callbackFlow {
        val listener = pitchesCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }
            val pitches = snapshot?.documents?.mapNotNull { it.toObject(Pitch::class.java)?.copy(id = it.id) } ?: emptyList()
            trySend(pitches)
        }
        awaitClose { listener.remove() }
    }

    suspend fun upsert(pitch: Pitch) {
        if (pitch.id.isEmpty()) {
            pitchesCollection.add(pitch)
        } else {
            pitchesCollection.document(pitch.id).set(pitch)
        }
    }

    suspend fun delete(pitch: Pitch) {
        if (pitch.id.isNotEmpty()) {
            pitchesCollection.document(pitch.id).delete()
        }
    }
}

