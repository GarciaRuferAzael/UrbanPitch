package com.example.urbanpitch.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

data class Coordinates(val latitude: Double, val longitude: Double)

class LocationService(private val ctx: Context) {

    private val fusedLocationClient = getFusedLocationProviderClient(ctx)
    private val locationManager = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val _coordinates = MutableStateFlow<Coordinates?>(null)
    val coordinates = _coordinates.asStateFlow()

    private val _isLoadingLocation = MutableStateFlow(false)
    val isLoadingLocation = _isLoadingLocation.asStateFlow()

    private var locationCallback: LocationCallback? = null

    suspend fun getCurrentLocation(usePreciseLocation: Boolean = false): Coordinates? {
        val permissionGranted = ContextCompat.checkSelfPermission(
            ctx,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!permissionGranted) throw SecurityException("Location permission not granted")
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            openLocationSettings()
        }

        _isLoadingLocation.value = true

        val location = withContext(Dispatchers.IO) {
            try {
                withTimeoutOrNull(8000) {
                    fusedLocationClient.getCurrentLocation(
                        if (usePreciseLocation) Priority.PRIORITY_HIGH_ACCURACY
                        else Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                        CancellationTokenSource().token
                    ).await()
                }
            } catch (e: Exception) {
                null
            } ?: try {
                fusedLocationClient.lastLocation.await()
            } catch (e: Exception) {
                null
            }
        }

        if (location != null) {
            val coords = Coordinates(location.latitude, location.longitude)
            _coordinates.value = coords
            _isLoadingLocation.value = false
            return coords
        }

        // Fallback: requestLocationUpdates
        return suspendCancellableCoroutine { cont ->
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val loc = result.lastLocation
                    if (loc != null) {
                        val coords = Coordinates(loc.latitude, loc.longitude)
                        _coordinates.value = coords
                        cont.resume(coords) {}
                        fusedLocationClient.removeLocationUpdates(this)
                        _isLoadingLocation.value = false
                    }
                }
            }

            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L)
                .setWaitForAccurateLocation(true)
                .setMinUpdateIntervalMillis(1000L)
                .build()

            fusedLocationClient.requestLocationUpdates(request, locationCallback!!, Looper.getMainLooper())
        }
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
