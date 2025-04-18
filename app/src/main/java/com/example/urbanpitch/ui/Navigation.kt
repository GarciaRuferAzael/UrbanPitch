package com.example.urbanpitch.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.urbanpitch.ui.screens.home.HomeScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

sealed interface UrbanPitchRoute {
    @Serializable data object HomeScreen : UrbanPitchRoute
    @Serializable data object DetailsScreen : UrbanPitchRoute
    @Serializable data object AddScreen : UrbanPitchRoute
    @Serializable data object ProfileScreen : UrbanPitchRoute
    @Serializable data object LoginScreen : UrbanPitchRoute
    @Serializable data object MapScreen : UrbanPitchRoute
    @Serializable data object FavouritesScreen : UrbanPitchRoute
}

@Composable
fun UrbanPitchNavGraph (navController: NavHostController) {
    val pitchVm = koinViewModel<PitchesViewModel>()
    val pitchesState by pitchVm.state.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = UrbanPitchRoute.HomeScreen
    ) {
        composable<UrbanPitchRoute.HomeScreen> {
            HomeScreen(pitchesState, navController)
        }
    }
}