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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.urbanpitch.data.remote.OSMDataSource
import androidx.navigation.toRoute
import com.example.urbanpitch.ui.screens.add.AddScreen
import com.example.urbanpitch.ui.screens.add.AddViewModel
import com.example.urbanpitch.ui.screens.details.DetailsScreen
import com.example.urbanpitch.ui.screens.home.HomeScreen
import com.example.urbanpitch.ui.screens.home.HomeViewModel
import com.example.urbanpitch.ui.screens.map.MapScreen
import com.example.urbanpitch.ui.screens.profile.ProfileScreen
//import com.example.urbanpitch.ui.screens.profile.ProfileViewModel
import com.example.urbanpitch.utils.Coordinates
import com.example.urbanpitch.utils.LocationService
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

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
            val homeViewModel: HomeViewModel = koinViewModel()
            HomeScreen(pitchesState, navController, homeViewModel)
        }

        composable<UrbanPitchRoute.Details> { backStackEntry ->
            val route = backStackEntry.toRoute<UrbanPitchRoute.Details>()
            val pitch = requireNotNull(pitchesState.pitches.find { it.id == route.pitchId })
            DetailsScreen(pitch, navController)
        }
        composable(UrbanPitchRoute.Map.toString()) {
            MapScreen(pitchesState, navController, )
        }
        composable(UrbanPitchRoute.Profile.toString()) {
            //val profileViewModel = viewModel<ProfileViewModel>()
            ProfileScreen(
                //viewModel = profileViewModel,
                navController = navController
            )
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
