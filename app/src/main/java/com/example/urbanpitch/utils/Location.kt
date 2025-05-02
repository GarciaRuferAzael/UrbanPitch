package com.example.urbanpitch.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

data class Coordinates(val latitude: Double, val longitude: Double)

class LocationService(private val ctx: Context) {
    private val fusedLocationClient = getFusedLocationProviderClient(ctx)
    private val locationManager = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val _coordinates = MutableStateFlow<Coordinates?>(null)
    val coordinates = _coordinates.asStateFlow()

    private val _isLoadingLocation = MutableStateFlow(false)
    val isLoadingLocation = _isLoadingLocation.asStateFlow()

    suspend fun getCurrentLocation(usePreciseLocation: Boolean = false): Coordinates? {
        val permissionGranted = ContextCompat.checkSelfPermission(
            ctx,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!permissionGranted) throw SecurityException("Location permission not granted")

        _isLoadingLocation.value = true

        val location = withContext(Dispatchers.IO) {
            try {
                fusedLocationClient.getCurrentLocation(
                    if (usePreciseLocation) Priority.PRIORITY_HIGH_ACCURACY
                    else Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    CancellationTokenSource().token
                ).await()
            } catch (e: Exception) {
                Log.e("LocationService", "getCurrentLocation() failed: ${e.message}")
                null
            } ?: try {
                // FALLBACK: ultima posizione nota
                fusedLocationClient.lastLocation.await()
            } catch (e: Exception) {
                Log.e("LocationService", "lastLocation fallback failed: ${e.message}")
                null
            }
        }

        _isLoadingLocation.value = false

        _coordinates.value = location?.let { Coordinates(it.latitude, it.longitude) }
        return _coordinates.value
    }


    fun openLocationSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        if (intent.resolveActivity(ctx.packageManager) != null) {
            ctx.startActivity(intent)
        }
    }
}
