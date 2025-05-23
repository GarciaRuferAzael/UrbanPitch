package com.example.urbanpitch.ui.screens.add

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.BrowseGallery
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.urbanpitch.data.database.Pitch
import com.example.urbanpitch.data.remote.OSMDataSource
import com.example.urbanpitch.ui.composables.BottomNavigationBar
import com.example.urbanpitch.ui.composables.ImageWithPlaceholder
import com.example.urbanpitch.ui.PitchesViewModel
import com.example.urbanpitch.ui.composables.AppBar
import com.example.urbanpitch.ui.composables.Size
import com.example.urbanpitch.utils.LocationService
import com.example.urbanpitch.utils.PermissionStatus
import com.example.urbanpitch.utils.copyUriToTempFile
import com.example.urbanpitch.utils.isOnline
import com.example.urbanpitch.utils.openWirelessSettings
import com.example.urbanpitch.utils.rememberCameraLauncher
import com.example.urbanpitch.utils.rememberMultiplePermissions
import com.example.urbanpitch.utils.saveImageToInternalStorage
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(navController: NavController,
              state: AddPitchState,
              actions: AddPitchActions,
              onSubmit: () -> Unit,
              pitchVm: PitchesViewModel = koinViewModel()) {

    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }


    val cameraLauncher = rememberCameraLauncher(
        onPictureTaken = { imageUrl ->
            actions.setImageUrl(imageUrl)
            selectedImageUri = Uri.parse(imageUrl.toString())
        }
    )


    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }


    val locationService = remember { LocationService(context) }

    val locationPermissions = rememberMultiplePermissions(
        listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    ) { statuses ->
        when {
            statuses.any { it.value == PermissionStatus.Granted } -> {}
            statuses.all { it.value == PermissionStatus.PermanentlyDenied } ->
                actions.setShowLocationPermissionPermanentlyDeniedSnackbar(true)
            else ->
                actions.setShowLocationPermissionDeniedAlert(true)
        }
    }

    val osmDataSource = koinInject<OSMDataSource>()

    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    fun getCurrentLocationName() = scope.launch {
        if (locationPermissions.statuses.none { it.value.isGranted }) {
            locationPermissions.launchPermissionRequest()
            return@launch
        }
        val coordinates = try {
            locationService.getCurrentLocation() ?: return@launch
        } catch (_: IllegalStateException) {
            actions.setShowLocationDisabledAlert(true)
            return@launch
        }
        if (!isOnline(context)) {
            actions.setShowNoInternetConnectivitySnackbar(true)
            return@launch
        }
        val place = osmDataSource.getPlace(coordinates)
        actions.setCity(place.displayName)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.get<Double>("selected_latitude")?.let { lat ->
            latitude = lat.toString()
            savedStateHandle.remove<Double>("selected_latitude")
        }
        savedStateHandle?.get<Double>("selected_longitude")?.let { lon ->
            longitude = lon.toString()
            savedStateHandle.remove<Double>("selected_longitude")
        }
        savedStateHandle?.get<String>("selected_city")?.let { selectedCity ->
            city = selectedCity
            savedStateHandle.remove<String>("selected_city")
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        topBar = { AppBar(navController, title = "Aggiungi Campo") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedImageUri != null) {
                        val tempFile = copyUriToTempFile(context, selectedImageUri!!)
                        if (tempFile != null) {
                            val fileUri = tempFile.toUri()

                            val storageRef = FirebaseStorage.getInstance().reference
                            val imageRef = storageRef.child("pitch_images/${UUID.randomUUID()}.jpg")

                            Log.d("UPLOAD", "Uploading from file URI: $fileUri")

                            val uploadTask = imageRef.putFile(fileUri)

                            uploadTask
                                .addOnSuccessListener {
                                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                                        val imageUrl = uri.toString()

                                        val pitch = Pitch(
                                            id = "",
                                            name = name,
                                            description = description,
                                            city = city,
                                            imageUrl = imageUrl,
                                            latitude = latitude.toFloatOrNull() ?: 0f,
                                            longitude = longitude.toFloatOrNull() ?: 0f
                                        )
                                        Log.d("aggiuntaDB", "Aggiunta pitch: $pitch")

                                        pitchVm.addPitch(pitch)
                                        
                                        navController.popBackStack()
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Errore upload immagine: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(context, "Errore nel creare il file temporaneo", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Seleziona o scatta un'immagine", Toast.LENGTH_SHORT).show()
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Check, contentDescription = "Salva")
            }

        }



    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val coroutineScope = rememberCoroutineScope()
            Button(onClick = {
                coroutineScope.launch {
                    val userCoords = locationService.getCurrentLocation()
                    Log.d("coordinate", "cordinate calcolate : $userCoords")
                    val lat = userCoords?.latitude?.toFloat() ?: 41.9028f
                    val lon = userCoords?.longitude?.toFloat() ?: 12.4964f

                    navController.navigate("select_location/$lat/$lon")
                }
            }) {
                Text("Seleziona posizione sulla mappa")
            }
            OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("Città") })
            OutlinedTextField(value = latitude, onValueChange = { latitude = it }, label = { Text("Latitudine") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = longitude, onValueChange = { longitude = it }, label = { Text("Longitudine") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

            Spacer(Modifier.size(8.dp))

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome") })
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descrizione") })

            Spacer(Modifier.size(8.dp))

            Button(onClick = { pickImageLauncher.launch("image/*") }) {
                Icon(
                    Icons.Outlined.Photo,
                    contentDescription = "Gallery icon",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Seleziona immagine")
            }
            Button(
                onClick = cameraLauncher::captureImage,
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            ) {
                Icon(
                    Icons.Outlined.PhotoCamera,
                    contentDescription = "Camera icon",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Scatta una foto")
            }
            selectedImageUri?.let { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = "Anteprima immagine",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }

        if (state.showLocationDisabledAlert) {
            AlertDialog(
                title = { Text("Location disabled") },
                text = { Text("Location must be enabled to get your current location in the app.") },
                confirmButton = {
                    TextButton(onClick = {
                        locationService.openLocationSettings()
                        actions.setShowLocationDisabledAlert(false)
                    }) {
                        Text("Enable")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { actions.setShowLocationDisabledAlert(false) }) {
                        Text("Dismiss")
                    }
                },
                onDismissRequest = { actions.setShowLocationDisabledAlert(false) }
            )
        }

        if (state.showLocationPermissionDeniedAlert) {
            AlertDialog(
                title = { Text("Location permission denied") },
                text = { Text("Location permission is required to get your current location in the app.") },
                confirmButton = {
                    TextButton(onClick = {
                        locationPermissions.launchPermissionRequest()
                        actions.setShowLocationPermissionDeniedAlert(false)
                    }) {
                        Text("Grant")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { actions.setShowLocationPermissionDeniedAlert(false) }) {
                        Text("Dismiss")
                    }
                },
                onDismissRequest = { actions.setShowLocationPermissionDeniedAlert(false) }
            )
        }

        if (state.showLocationPermissionPermanentlyDeniedSnackbar) {
            LaunchedEffect(snackbarHostState) {
                val res = snackbarHostState.showSnackbar(
                    "Location permission is required.",
                    "Go to Settings",
                    duration = SnackbarDuration.Long
                )
                if (res == SnackbarResult.ActionPerformed) {
                    context.startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    )
                }
                actions.setShowLocationPermissionPermanentlyDeniedSnackbar(false)
            }
        }

        if (state.showNoInternetConnectivitySnackbar) {
            LaunchedEffect(snackbarHostState) {
                val res = snackbarHostState.showSnackbar(
                    message = "No Internet connectivity",
                    actionLabel = "Go to Settings",
                    duration = SnackbarDuration.Long
                )
                if (res == SnackbarResult.ActionPerformed) {
                    openWirelessSettings(context)
                }
                actions.setShowNoInternetConnectivitySnackbar(false)
            }
        }
    }
}
