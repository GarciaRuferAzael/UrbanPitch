package com.example.urbanpitch.utils

import android.content.Context
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient

data class Coordinates(val latitude: Double, val longitude: Double)

class LocationService(private val ctx: Context) {
    private val fusedLocationClient =
        getFusedLocationProviderClient(ctx)
}