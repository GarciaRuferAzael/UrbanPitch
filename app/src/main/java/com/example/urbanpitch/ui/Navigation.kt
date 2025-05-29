package com.example.urbanpitch.ui

import SelectLocationOSMScreen
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.example.urbanpitch.ui.screens.favourites.FavoritesScreen
import com.example.urbanpitch.ui.screens.favourites.FavoritesViewModel
import com.example.urbanpitch.ui.screens.home.HomeScreen
import com.example.urbanpitch.ui.screens.home.HomeViewModel
import com.example.urbanpitch.ui.screens.login.AuthViewModel
import com.example.urbanpitch.ui.screens.map.MapScreen
import com.example.urbanpitch.ui.screens.profile.ProfileScreen
import com.example.urbanpitch.ui.screens.login.LoginScreen
import com.example.urbanpitch.ui.screens.login.RegisterScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

sealed interface UrbanPitchRoute {
    @Serializable data object Home : UrbanPitchRoute
    @Serializable data class Details(val pitchId : String) : UrbanPitchRoute
    @Serializable data object Add : UrbanPitchRoute
    @Serializable data object Profile : UrbanPitchRoute
    @Serializable data object Login : UrbanPitchRoute
    @Serializable data object Register : UrbanPitchRoute
    @Serializable data object Map : UrbanPitchRoute
    @Serializable data object Favorites : UrbanPitchRoute
}

@Composable
fun UrbanPitchNavGraph(navController: NavHostController, startDestination: String = UrbanPitchRoute.Login.toString()) {
    val pitchVm = koinViewModel<PitchesViewModel>()
    val pitchesState by pitchVm.state.collectAsStateWithLifecycle()
    val authViewModel = koinViewModel<AuthViewModel>()
    val isAuthenticated = authViewModel.isAuthenticated

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(UrbanPitchRoute.Login.toString()) {
            val context = LocalContext.current
            val authViewModel: AuthViewModel = koinViewModel()
            val loginResult by authViewModel.loginResult.collectAsState()

            LaunchedEffect(loginResult) {
                loginResult.onSuccess { success ->
                    if (success) {
                        navController.navigate(UrbanPitchRoute.Home.toString()) {
                            popUpTo(UrbanPitchRoute.Login.toString()) { inclusive = true }
                        }
                    }
                }.onFailure { error ->
                    Toast.makeText(context, "Errore login: ${error.message}", Toast.LENGTH_LONG).show()
                }
            }

            LoginScreen(
                navController = navController,
                onLogin = { email, password ->
                    authViewModel.login(email, password)
                },
                onRegister = {
                    navController.navigate(UrbanPitchRoute.Register.toString())
                }
            )
        }


        composable(UrbanPitchRoute.Register.toString()) {
            val context = LocalContext.current
            RegisterScreen(
                navController = navController,
                onRegister = { username, email, password ->
                    authViewModel.register(
                        username = username,
                        email = email,
                        password = password,
                        onSuccess = {
                            navController.navigate(UrbanPitchRoute.Home.toString()) {
                                popUpTo(UrbanPitchRoute.Register.toString()) { inclusive = true }
                            }
                        },
                        onError = { error ->
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        }
                    )
                },
                onBack = {
                    navController.navigate(UrbanPitchRoute.Login.toString())
                }
            )
        }


        composable(UrbanPitchRoute.Home.toString()) {
            val pitchesViewModel: PitchesViewModel = koinViewModel()
            val favoritesViewModel: FavoritesViewModel = koinViewModel()
            val homeViewModel: HomeViewModel = koinViewModel()

            val state by pitchesViewModel.state.collectAsState()

            HomeScreen(
                state = state,
                navController = navController,
                viewModel = homeViewModel,
                favoritesViewModel = favoritesViewModel
            )
        }

        composable(UrbanPitchRoute.Favorites.toString()) {
            val pitchesViewModel: PitchesViewModel = koinViewModel()
            val favoritesViewModel: FavoritesViewModel = koinViewModel()

            val state by pitchesViewModel.state.collectAsState()

            FavoritesScreen(
                favoritesVm = favoritesViewModel,
                pitches = state.pitches,
                navController = navController
            )
        }

        composable<UrbanPitchRoute.Details> { backStackEntry ->
            val route = backStackEntry.toRoute<UrbanPitchRoute.Details>()

            // 🔥 CORREZIONE: cerca usando id STRING, non più int
            val pitch = requireNotNull(pitchesState.pitches.find { it.id == route.pitchId })

            DetailsScreen(pitch, navController)

        }

        composable(UrbanPitchRoute.Map.toString()) {
            MapScreen(navController)
        }

        composable(UrbanPitchRoute.Profile.toString()) {
            ProfileScreen(navController = navController)
        }

        composable(UrbanPitchRoute.Add.toString()) {
            val addPitchVm = koinViewModel<AddViewModel>()
            val state by addPitchVm.state.collectAsStateWithLifecycle()
            AddScreen(
                navController,
                state,
                addPitchVm.actions,
                onSubmit = { pitchVm.addPitch(state.toPitch()) } // Salva su Firestore
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


