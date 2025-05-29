package com.example.urbanpitch.ui.screens.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanpitch.data.repositories.FavoritesRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: FavoritesRepository
) : ViewModel() {

    private val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    val favoritePitchIds = repository.getFavorites(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavorite(pitchId: String) = viewModelScope.launch {
        if (pitchId in favoritePitchIds.value) {
            repository.removeFavorite(userId, pitchId)
        } else {
            repository.addFavorite(userId, pitchId)
        }
    }

    fun isFavorite(pitchId: String): Boolean {
        return pitchId in favoritePitchIds.value
    }
}

