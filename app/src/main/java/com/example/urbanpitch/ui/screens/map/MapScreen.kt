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
import com.example.urbanpitch.data.repositories.PitchesRepository
import com.example.urbanpitch.ui.composables.BottomNavigationBar
import com.example.urbanpitch.ui.PitchesState
import com.example.urbanpitch.ui.UrbanPitchRoute
import com.example.urbanpitch.ui.composables.AppBar
import com.example.urbanpitch.utils.Coordinates
import com.example.urbanpitch.utils.LocationService
import com.example.urbanpitch.utils.resizeDrawable
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.config.Configuration


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    state: PitchesState,
    navController: NavController
) {
    val context = LocalContext.current
    val map = remember { MapView(context) }
    val pitches = state.pitches

    val locationService = remember { LocationService(context) }
    val userLocation = remember { mutableStateOf<Coordinates?>(null) }

    // Recupera posizione in modo sicuro
    LaunchedEffect(Unit) {
        val location = locationService.getCurrentLocation()
        userLocation.value = location
    }

    // Posizione di default: Roma
    val lat = userLocation.value?.latitude?.toFloat() ?: 41.9028f
    val lon = userLocation.value?.longitude?.toFloat() ?: 12.4964f

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
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Aggiungi Campo"
                )
            }
        }
    ) { contentPadding ->
        Box(modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()) {

            AndroidView(factory = {
                Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
                map.setTileSource(TileSourceFactory.MAPNIK)
                map.setMultiTouchControls(true)

                val controller = map.controller
                controller.setZoom(15.0)
                controller.setCenter(GeoPoint(lat.toDouble(), lon.toDouble()))

                // Marker dei pitch
                pitches.forEach { pitch ->
                    val marker = Marker(map)
                    marker.position = GeoPoint(pitch.latitude.toDouble(), pitch.longitude.toDouble())
                    marker.title = pitch.name
                    marker.subDescription = pitch.description
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                    val icon = resizeDrawable(context, R.drawable.football_marker, 80, 80)
                    marker.icon = icon

                    // 🔥 Listener per click sul marker
                    marker.setOnMarkerClickListener { _, _ ->
                        navController.navigate(UrbanPitchRoute.Details(pitch.id))
                        true // evita comportamento di default
                    }

                    map.overlays.add(marker)
                }

                map
            }, update = { mapView ->
                mapView.controller.setCenter(GeoPoint(lat.toDouble(), lon.toDouble()))
                mapView.invalidate()
            })
        }
    }
}
