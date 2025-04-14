package com.example.urbanpitch

import android.app.Application
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.android.ext.koin.androidContext

class UrbanPitchApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@UrbanPitchApplication)
            modules(appModule)
        }
    }
}