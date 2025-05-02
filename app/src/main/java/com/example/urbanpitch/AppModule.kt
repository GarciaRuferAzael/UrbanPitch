package com.example.urbanpitch

import androidx.room.Room
import com.example.urbanpitch.data.database.UrbanPitchDatabase
import com.example.urbanpitch.data.remote.OSMDataSource
import com.example.urbanpitch.data.repositories.PitchesRepository
import com.example.urbanpitch.ui.PitchesViewModel
import com.example.urbanpitch.ui.screens.add.AddViewModel
import com.example.urbanpitch.ui.screens.home.HomeViewModel
import com.example.urbanpitch.utils.LocationService
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Database Room
    single {
        Room.databaseBuilder(
            get(),
            UrbanPitchDatabase::class.java,
            "urban-pitch"
        ).fallbackToDestructiveMigration()
            .build()
    }

    // DAO + Repository
    single { get<UrbanPitchDatabase>().pitchesDAO() }
    single { PitchesRepository(get()) }

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

    // ViewModel
    viewModel { PitchesViewModel(get()) }
    viewModel { AddViewModel() }
    viewModel { HomeViewModel(get()) } // usa LocationService
}
