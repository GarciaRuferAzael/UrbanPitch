package com.example.urbanpitch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanpitch.data.database.Pitch
import com.example.urbanpitch.data.repositories.PitchesRepositoryFirebase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PitchesState(
    val pitches: List<Pitch> = emptyList()
)

class PitchesViewModel(
    private val repository: PitchesRepositoryFirebase  // Firebase repository, non Room repository!
) : ViewModel() {

    val state = repository.getAll()
        .map { PitchesState(pitches = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PitchesState(emptyList())
        )

    fun addPitch(pitch: Pitch) = viewModelScope.launch {
        repository.upsert(pitch)
    }

    fun deletePitch(pitch: Pitch) = viewModelScope.launch {
        repository.delete(pitch)
    }
}
