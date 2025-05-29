package com.example.urbanpitch.ui.screens.favourites

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.urbanpitch.data.database.Pitch
import com.example.urbanpitch.ui.UrbanPitchRoute
import com.example.urbanpitch.ui.composables.AppBar
import com.example.urbanpitch.ui.composables.BottomNavigationBar
import com.example.urbanpitch.ui.screens.home.NoItemsPlaceholder
import com.example.urbanpitch.ui.screens.home.PitchItem

@Composable
fun FavoritesScreen(
    favoritesVm: FavoritesViewModel,
    pitches: List<Pitch>,
    navController: NavController
) {
    val favoriteIds by favoritesVm.favoritePitchIds.collectAsState()
    val favoritePitches = remember(favoriteIds, pitches) {
        pitches.filter { it.id in favoriteIds }
    }

    Scaffold(
        topBar = { AppBar(navController, title = "Preferiti") },
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        if (favoritePitches.isEmpty()) {
            NoItemsPlaceholder(Modifier.padding(padding))
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(favoritePitches) { pitch ->
                    PitchItem(
                        pitch = pitch,
                        isFavorite = true,
                        onToggleFavorite = { favoritesVm.toggleFavorite(pitch.id) },
                        onClick = { navController.navigate(UrbanPitchRoute.Details(pitch.id)) }
                    )
                }
            }
        }
    }
}

