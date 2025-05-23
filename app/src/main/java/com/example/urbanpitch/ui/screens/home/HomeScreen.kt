package com.example.urbanpitch.ui.screens.home

import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.urbanpitch.data.database.Pitch
import com.example.urbanpitch.ui.composables.BottomNavigationBar
import com.example.urbanpitch.ui.PitchesState
import com.example.urbanpitch.ui.UrbanPitchRoute
import com.example.urbanpitch.ui.composables.AppBar
import com.example.urbanpitch.utils.Coordinates
import com.example.urbanpitch.utils.LocationService
import com.example.urbanpitch.utils.PermissionStatus
import com.example.urbanpitch.utils.rememberMultiplePermissions
import kotlinx.coroutines.launch
import java.io.Console

@Composable
fun HomeScreen(state: PitchesState, navController: NavController, viewModel: HomeViewModel) {
    val context = LocalContext.current

    val userLocation by viewModel.userLocation.collectAsState()

    // 👇 Permessi localizzazione
    val locationPermissionHandler = rememberMultiplePermissions(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    ) { statuses ->
        val allGranted = statuses.all { it.value == PermissionStatus.Granted }
        if (!allGranted) {
            Toast.makeText(context, "Permessi di localizzazione richiesti per calcolare la distanza", Toast.LENGTH_SHORT).show()
        }
    }

    // 👇 Richiedi permessi appena entri
    LaunchedEffect(Unit) {
        val allGranted = locationPermissionHandler.statuses.all { it.value == PermissionStatus.Granted }
        if (!allGranted) {
            locationPermissionHandler.launchPermissionRequest()
        } else {
            viewModel.loadLocation() // carica posizione solo se permessi OK
        }
    }

    Scaffold(
        topBar = { AppBar(navController, title = "UrbanPitch") },
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

        if (state.pitches.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 80.dp),
                modifier = Modifier.padding(contentPadding)
            ) {
                items(state.pitches) { item ->
                    PitchItemWithDistance(
                        pitch = item,
                        userCoords = userLocation,
                        onClick = { navController.navigate(UrbanPitchRoute.Details(item.id)) }
                    )
                }
            }
        } else {
            NoItemsPlaceholder(Modifier.padding(contentPadding))
        }
    }
}

@Composable
fun PitchItemWithDistance(
    pitch: Pitch,
    userCoords: Coordinates?,
    onClick: () -> Unit
) {
    val distance = remember(userCoords) {
        userCoords?.let {
            val pitchCoords = Coordinates(pitch.latitude.toDouble(), pitch.longitude.toDouble())

            calculateDistanceInKm(it, pitchCoords)
        }
    }

    PitchItem(pitch = pitch, distanceInKm = distance, onClick = onClick)
}



@Composable
fun PitchItem(pitch: Pitch, distanceInKm: Double? = 0.0, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = pitch.imageUrl,
                contentDescription = pitch.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = pitch.name, style = MaterialTheme.typography.titleMedium)
                Text(text = pitch.city, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Distanza: %.1f km".format(distanceInKm),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

fun calculateDistanceInKm(
    start: Coordinates,
    end: Coordinates
): Double {
    val result = FloatArray(1)
    Location.distanceBetween(
        start.latitude.toDouble(), start.longitude.toDouble(),
        end.latitude.toDouble(), end.longitude.toDouble(),
        result
    )
    return result[0] / 1000.0 // converti da metri a km
}

@Composable
fun NoItemsPlaceholder(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            Icons.Outlined.LocationOn, "Location icon",
            modifier = Modifier.padding(bottom = 16.dp).size(48.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
        Text(
            "No pitches",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            "Tap the + button to add a new pitch.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
