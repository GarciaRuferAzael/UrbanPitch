package com.example.urbanpitch.ui.screens.home

import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.urbanpitch.data.database.Pitch
import com.example.urbanpitch.data.database.PitchFilter
import com.example.urbanpitch.data.repositories.FavoritesRepository
import com.example.urbanpitch.ui.composables.BottomNavigationBar
import com.example.urbanpitch.ui.PitchesState
import com.example.urbanpitch.ui.UrbanPitchRoute
import com.example.urbanpitch.ui.composables.AppBar
import com.example.urbanpitch.ui.screens.favourites.FavoritesViewModel
import com.example.urbanpitch.utils.Coordinates
import com.example.urbanpitch.utils.LocationService
import com.example.urbanpitch.utils.PermissionStatus
import com.example.urbanpitch.utils.rememberMultiplePermissions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf
import java.io.Console

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: PitchesState,
    navController: NavController,
    viewModel: HomeViewModel,
    favoritesViewModel: FavoritesViewModel
) {
    val context = LocalContext.current
    val userLocation by viewModel.userLocation.collectAsState()

    val filter by viewModel.filter.collectAsState()
    val filteredPitches = viewModel.getFilteredPitches(state.pitches)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showFilterSheet by remember { mutableStateOf(false) }


    FilterBar(
        filter = filter,
        onFilterChange = { viewModel.setFilter(it)
            showFilterSheet = false
        }
    )

    val locationPermissionHandler = rememberMultiplePermissions(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    ) { statuses ->
        val allGranted = statuses.all { it.value == PermissionStatus.Granted }
        if (!allGranted) {
            Toast.makeText(context, "Permessi di localizzazione richiesti", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        val allGranted = locationPermissionHandler.statuses.all { it.value == PermissionStatus.Granted }
        if (!allGranted) {
            locationPermissionHandler.launchPermissionRequest()
        } else {
            viewModel.loadLocation()
        }
    }

    val favoriteIds by favoritesViewModel.favoritePitchIds.collectAsState()

    Scaffold(
        topBar = { AppBar(
            navController,
            title = "UrbanPitch",
            onFilterClick = { showFilterSheet = true }
        ) },
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(UrbanPitchRoute.Add.toString()) },
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
                items(filteredPitches) { pitch ->
                    PitchItemWithDistance(
                        pitch = pitch,
                        userCoords = userLocation,
                        isFavorite = pitch.id in favoriteIds,
                        onToggleFavorite = { favoritesViewModel.toggleFavorite(pitch.id) },
                        onClick = { navController.navigate(UrbanPitchRoute.Details(pitch.id)) }
                    )
                }
            }
        } else {
            NoItemsPlaceholder(Modifier.padding(contentPadding))
        }
    }
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState
        ) {
            FilterBar(
                filter = filter,
                onFilterChange = {
                    viewModel.setFilter(it)
                }
            )
        }
    }

}


@Composable
fun PitchItemWithDistance(
    pitch: Pitch,
    userCoords: Coordinates?,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    val distance = remember(userCoords) {
        userCoords?.let {
            val pitchCoords = Coordinates(pitch.latitude.toDouble(), pitch.longitude.toDouble())
            calculateDistanceInKm(it, pitchCoords)
        }
    }

    PitchItem(
        pitch = pitch,
        distanceInKm = distance,
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onClick = onClick
    )
}

@Composable
fun PitchItem(
    pitch: Pitch,
    distanceInKm: Double? = 0.0,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = pitch.imageUrl,
                    contentDescription = pitch.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.White.copy(alpha = 0.6f), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Preferito",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
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

@Composable
fun FilterBar(
    filter: PitchFilter,
    onFilterChange: (PitchFilter) -> Unit
) {
    var city by remember { mutableStateOf(filter.city ?: "") }
    var maxDistance by remember { mutableStateOf(filter.maxDistanceKm ?: 50.0) }

    Column(Modifier.padding(8.dp)) {
        Text("Filtri", style = MaterialTheme.typography.titleSmall)

        OutlinedTextField(
            value = city,
            onValueChange = {
                city = it
                onFilterChange(filter.copy(city = it))
            },
            label = { Text("Città") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Distanza massima: ${maxDistance.toInt()} km")
        Slider(
            value = maxDistance.toFloat(),
            onValueChange = {
                maxDistance = it.toDouble()
                onFilterChange(filter.copy(maxDistanceKm = it.toDouble()))
            },
            valueRange = 1f..100f,
            steps = 9
        )
    }
}
