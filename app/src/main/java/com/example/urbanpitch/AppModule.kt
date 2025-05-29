package com.example.urbanpitch

import com.example.urbanpitch.data.remote.OSMDataSource
import com.example.urbanpitch.data.repositories.FavoritesRepository
import com.example.urbanpitch.data.repositories.PitchesRepositoryFirebase
import com.example.urbanpitch.ui.PitchesViewModel
import com.example.urbanpitch.ui.screens.add.AddViewModel
import com.example.urbanpitch.ui.screens.favourites.FavoritesViewModel
import com.example.urbanpitch.ui.screens.home.HomeViewModel
import com.example.urbanpitch.ui.screens.login.AuthViewModel
import com.example.urbanpitch.ui.screens.map.MapViewModel
import com.example.urbanpitch.utils.LocationService
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Firebase Repository
    single { PitchesRepositoryFirebase() }

    // HttpClient e OSM
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }
    single { OSMDataSource(get()) }

    single { LocationService(androidContext()) }

    single { FavoritesRepository() }

    // ViewModel

    viewModel { FavoritesViewModel(get()) }

    viewModel { PitchesViewModel(get()) }

    viewModel { AddViewModel() }

    viewModel { HomeViewModel(get()) }

    viewModel { MapViewModel(get()) }

    viewModel { AuthViewModel() }
}