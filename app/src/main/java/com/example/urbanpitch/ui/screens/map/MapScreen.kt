package com.example.urbanpitch.ui.screens.map

import android.annotation.SuppressLint
import android.preference.PreferenceManager
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.urbanpitch.R
import com.example.urbanpitch.data.database.Pitch
import com.example.urbanpitch.ui.composables.BottomNavigationBar
import com.example.urbanpitch.ui.PitchesState
import com.example.urbanpitch.ui.UrbanPitchRoute
import com.example.urbanpitch.ui.composables.AppBar
import com.example.urbanpitch.utils.Coordinates
import com.example.urbanpitch.utils.LocationService
import com.example.urbanpitch.utils.resizeDrawable
import org.koin.androidx.compose.koinViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.config.Configuration


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val mapViewModel: MapViewModel = koinViewModel()
    val pitches by mapViewModel.pitches.collectAsState()

    val locationService = remember { LocationService(context) }
    val userLocation = remember { mutableStateOf<Coordinates?>(null) }

    LaunchedEffect(Unit) {
        val location = locationService.getCurrentLocation()
        userLocation.value = location
    }

    val lat = userLocation.value?.latitude?.toFloat() ?: 41.9028f
    val lon = userLocation.value?.longitude?.toFloat() ?: 12.4964f

    // Mantieni una reference alla MapView
    val mapViewRef = remember { mutableStateOf<MapView?>(null) }

    Scaffold(
        topBar = { AppBar(navController, title = "Mappa") },
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(UrbanPitchRoute.Add.toString())
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Aggiungi Campo")
            }
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            AndroidView(factory = {
                Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
                val map = MapView(context)
                map.setTileSource(TileSourceFactory.MAPNIK)
                map.setMultiTouchControls(true)
                map.controller.setZoom(15.0)
                map.controller.setCenter(GeoPoint(lat.toDouble(), lon.toDouble()))
                mapViewRef.value = map
                map
            }, update = { mapView ->
                mapView.controller.setCenter(GeoPoint(lat.toDouble(), lon.toDouble()))
                mapView.overlays.clear() // pulizia marker vecchi

                pitches.forEach { pitch ->
                    val marker = Marker(mapView)
                    marker.position = GeoPoint(pitch.latitude.toDouble(), pitch.longitude.toDouble())
                    marker.title = pitch.name
                    marker.subDescription = pitch.description
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                    val icon = resizeDrawable(context, R.drawable.football_marker, 60, 60)
                    marker.icon = icon

                    marker.setOnMarkerClickListener { _, _ ->
                        navController.navigate(UrbanPitchRoute.Details(pitch.id))
                        true
                    }

                    mapView.overlays.add(marker)
                }

                mapView.invalidate()
            })
        }
    }
}

