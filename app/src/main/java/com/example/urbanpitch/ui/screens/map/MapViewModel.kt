package com.example.urbanpitch.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.urbanpitch.data.database.Pitch
import com.example.urbanpitch.data.repositories.PitchesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    private val repository: PitchesRepository
) : ViewModel() {

    private val _pitches = MutableStateFlow<List<Pitch>>(emptyList())
    val pitches: StateFlow<List<Pitch>> = _pitches

    init {
        observePitches()
    }

    private fun observePitches() {
        viewModelScope.launch {
            repository.pitches.collect {
                _pitches.value = it
            }
        }
    }
}

class MapViewModelFactory(
    private val repository: PitchesRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
