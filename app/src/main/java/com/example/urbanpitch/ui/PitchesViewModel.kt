package com.example.urbanpitch.ui

import androidx.lifecycle.ViewModel
import com.example.urbanpitch.data.database.Pitch
import com.example.urbanpitch.data.repositories.PitchRepository


data class PitchesState(val pitches: List<Pitch>)

class PitchesViewModel(private val repository: PitchRepository): ViewModel() {

    val state = repository.pitches.map { PitchesState(pitches = it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = PitchesState(emptyList())
    )

    fun addPitch(pitch: Pitch) = viewModelScope.launch {
        repository.upsert(pitch)
    }

    fun deletePitch(pitch: Pitch) = viewModelScope.launch {
        repository.delete(pitch)
    }

}
