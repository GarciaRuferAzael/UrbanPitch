package com.example.urbanpitch.ui

import SelectLocationOSMScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.urbanpitch.data.remote.OSMDataSource
import com.example.urbanpitch.ui.screens.add.AddScreen
import com.example.urbanpitch.ui.screens.add.AddViewModel
import com.example.urbanpitch.ui.screens.home.HomeScreen
import com.example.urbanpitch.ui.screens.map.MapScreen
import com.example.urbanpitch.ui.screens.profile.ProfileScreen
import com.example.urbanpitch.utils.Coordinates
import com.example.urbanpitch.utils.LocationService
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

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
fun UrbanPitchNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    val pitchVm = koinViewModel<PitchesViewModel>()
    val pitchesState by pitchVm.state.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = UrbanPitchRoute.HomeScreen.toString(),
        modifier = modifier
    ) {
        composable(UrbanPitchRoute.HomeScreen.toString()) {
            HomeScreen(pitchesState, navController)
        }
        composable(UrbanPitchRoute.MapScreen.toString()) {
            MapScreen(pitchesState, navController)
        }
        composable(UrbanPitchRoute.ProfileScreen.toString()) {
            ProfileScreen(pitchesState, navController)
        }
        composable(UrbanPitchRoute.AddScreen.toString()) {
            val addPitchVm = koinViewModel<AddViewModel>()
            val state by addPitchVm.state.collectAsStateWithLifecycle()
            AddScreen(
                navController,
                state,
                addPitchVm.actions,
                onSubmit = { pitchVm.addPitch(state.toPitch()) }
            )
        }

        composable(
            "select_location/{lat}/{lon}",
            arguments = listOf(
                navArgument("lat") { type = NavType.FloatType },
                navArgument("lon") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 41.9028
            val lon = backStackEntry.arguments?.getFloat("lon")?.toDouble() ?: 12.4964

            val osmDataSource = koinInject<OSMDataSource>()

            SelectLocationOSMScreen(
                initialLatitude = lat,
                initialLongitude = lon,
                navController = navController,
                osmDataSource = osmDataSource
            )
        }


    }
}
