package com.example.urbanpitch

import androidx.room.Room
import com.example.urbanpitch.data.database.UrbanPitchDatabase
import com.example.urbanpitch.data.repositories.PitchesRepository
import com.example.urbanpitch.ui.PitchesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single {
        Room.databaseBuilder(
            get(),
            UrbanPitchDatabase::class.java,
            "urban-pitch"
        ).build()
    }

    single { get<UrbanPitchDatabase>().pitchesDAO() }

    single { PitchesRepository(get()) }

    viewModel { PitchesViewModel( get() ) }
}