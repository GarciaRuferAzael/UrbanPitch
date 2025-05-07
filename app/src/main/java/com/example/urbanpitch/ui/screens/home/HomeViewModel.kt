package com.example.urbanpitch.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanpitch.utils.Coordinates
import com.example.urbanpitch.utils.LocationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val locationService: LocationService
) : ViewModel() {

    private val _userLocation = MutableStateFlow<Coordinates?>(null)
    val userLocation = _userLocation.asStateFlow()

    fun loadLocation() {
        viewModelScope.launch {
            try {
                val location = locationService.getCurrentLocation()

                _userLocation.value = location
                Log.d("HomeViewModel", "My Location: $location")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Errore nel recupero della posizione: ${e.message}")
            }
        }
    }
}
