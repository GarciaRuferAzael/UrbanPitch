package com.example.urbanpitch.ui.screens.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanpitch.data.database.Pitch
import com.example.urbanpitch.data.database.PitchFilter
import com.example.urbanpitch.data.repositories.PitchesRepositoryFirebase
import com.example.urbanpitch.utils.Coordinates
import com.example.urbanpitch.utils.LocationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val locationService: LocationService
) : ViewModel() {

    private val _userLocation = MutableStateFlow<Coordinates?>(null)
    val userLocation = _userLocation.asStateFlow()

    private val _filter = MutableStateFlow(PitchFilter())
    val filter = _filter.asStateFlow()

    fun setFilter(newFilter: PitchFilter) {
        _filter.value = newFilter
    }

    fun loadLocation() {
        viewModelScope.launch {
            try {
                val location = locationService.getCurrentLocation()
                _userLocation.value = location
                Log.d("HomeViewModel", "My Location: $location")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Location error: ${e.message}")
            }
        }
    }

    fun getFilteredPitches(
        pitches: List<Pitch>,
    ): List<Pitch> {
        val currentFilter = _filter.value
        val currentLocation = _userLocation.value

        return pitches.filter { pitch ->
            val matchesCity = currentFilter.city.isNullOrEmpty() ||
                    pitch.city.contains(currentFilter.city!!, ignoreCase = true)

            val withinDistance = currentFilter.maxDistanceKm?.let {
                currentLocation?.let { userLoc ->
                    val pitchCoords = Coordinates(pitch.latitude.toDouble(), pitch.longitude.toDouble())
                    calculateDistanceInKm(userLoc, pitchCoords) <= it
                } ?: false
            } ?: true

            matchesCity && withinDistance
        }
    }
}

