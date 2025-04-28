package com.example.urbanpitch.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.urbanpitch.ui.screens.add.AddScreen
import com.example.urbanpitch.ui.screens.add.AddViewModel
import com.example.urbanpitch.ui.screens.details.DetailsScreen
import com.example.urbanpitch.ui.screens.home.HomeScreen
import com.example.urbanpitch.ui.screens.map.MapScreen
import com.example.urbanpitch.ui.screens.profile.ProfileScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

sealed interface UrbanPitchRoute {
    @Serializable data object Home : UrbanPitchRoute
    @Serializable data class Details(val pitchId : Int) : UrbanPitchRoute
    @Serializable data object Add : UrbanPitchRoute
    @Serializable data object Profile : UrbanPitchRoute
    @Serializable data object Login : UrbanPitchRoute
    @Serializable data object Map : UrbanPitchRoute
    @Serializable data object Favourites : UrbanPitchRoute
}

@Composable
fun UrbanPitchNavGraph(navController: NavHostController) {
    val pitchVm = koinViewModel<PitchesViewModel>()
    val pitchesState by pitchVm.state.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = UrbanPitchRoute.Home.toString()
    ) {
        composable(UrbanPitchRoute.Home.toString()) {
            HomeScreen(pitchesState, navController)
        }
        composable<UrbanPitchRoute.Details> { backStackEntry ->
            val route = backStackEntry.toRoute<UrbanPitchRoute.Details>()
            val pitch = requireNotNull(pitchesState.pitches.find { it.id == route.pitchId })
            DetailsScreen(pitch, navController)
        }
        composable(UrbanPitchRoute.Map.toString()) {
            MapScreen(pitchesState, navController)
        }
        composable(UrbanPitchRoute.Profile.toString()) {
            ProfileScreen(pitchesState, navController)
        }
        composable(UrbanPitchRoute.Add.toString()) {
            val addPitchVm = koinViewModel<AddViewModel>()
            val state by addPitchVm.state.collectAsStateWithLifecycle()
            AddScreen(
                navController,
                state,
                addPitchVm.actions,
                onSubmit = { pitchVm.addPitch(state.toPitch()) }
            )
        }

    }
}
