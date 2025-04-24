package com.example.urbanpitch.ui.screens.home

import android.location.Location
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(state: PitchesState, navController: NavController) {

    val context = LocalContext.current
    val locationService = remember { LocationService(context) }

    Scaffold(
        topBar = { AppBar(navController, title = "UrbanPitch")},
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(UrbanPitchRoute.AddScreen.toString())
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
        if (state.pitches.isNotEmpty()) {
            LazyVerticalGrid (
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp,8.dp, 8.dp,80.dp),
                modifier = Modifier.padding(contentPadding)
            )  {
                items(state.pitches) { item ->
                    PitchItemWithDistance(item, locationService, onClick = {})
                }
            }
        } else {
            Text("nessun item trovato")
        }
    }
}

@Composable
fun PitchItemWithDistance(pitch: Pitch, locationService: LocationService, onClick: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var distance by remember { mutableStateOf<Double?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val userCoords = locationService.getCurrentLocation()
                if (userCoords != null) {
                    val pitchCoords = Coordinates(pitch.latitude.toDouble(), pitch.longitude.toDouble())
                    distance = calculateDistanceInKm(userCoords, pitchCoords)
                }
            } catch (e: Exception) {
                // Gestisci permessi non concessi, posizione disattivata, ecc.
            }
        }
    }
    PitchItem(pitch = pitch, distanceInKm = distance ?: 0.0, onClick = onClick)
}

@Composable
fun PitchItem(pitch: Pitch, distanceInKm: Double = 0.0, onClick: () -> Unit) {
    Card(
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
