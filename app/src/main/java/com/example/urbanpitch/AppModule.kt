package com.example.urbanpitch

import androidx.room.Room
import com.example.urbanpitch.data.database.UrbanPitchDatabase
import com.example.urbanpitch.data.remote.OSMDataSource
import com.example.urbanpitch.data.repositories.PitchesRepository
import com.example.urbanpitch.ui.PitchesViewModel
import com.example.urbanpitch.ui.screens.add.AddViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single {
        Room.databaseBuilder(
            get(),
            UrbanPitchDatabase::class.java,
            "urban-pitch"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    single { OSMDataSource(get()) }

    single { get<UrbanPitchDatabase>().pitchesDAO() }

    single { PitchesRepository(get()) }

    viewModel { PitchesViewModel( get() ) }

    viewModel { AddViewModel() }
}