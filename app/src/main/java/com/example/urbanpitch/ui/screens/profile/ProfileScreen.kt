package com.example.urbanpitch.ui.screens.profile

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.urbanpitch.data.database.User
import com.example.urbanpitch.ui.UrbanPitchRoute
import com.example.urbanpitch.ui.composables.BottomNavigationBar
import com.example.urbanpitch.ui.screens.login.AuthViewModel
import com.example.urbanpitch.utils.LocationService
import com.example.urbanpitch.utils.PermissionStatus
import com.example.urbanpitch.utils.rememberMultiplePermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = koinViewModel()
) {
    val user by authViewModel.currentUser.collectAsState()
    val context = LocalContext.current
    val onLogout = {
        authViewModel.logout()
        navController.navigate(UrbanPitchRoute.Login.toString()) {
            popUpTo(UrbanPitchRoute.Home.toString()) { inclusive = true }
        }
    }
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
        }
    }

    Scaffold(bottomBar = { BottomNavigationBar(navController) }) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Profilo",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.Start)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    user?.let { u ->
                        val imageUri = u.profileImageUri
                        if (!imageUri.isNullOrBlank()) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Default Avatar",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                    .padding(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = u.username.ifBlank { "Utente" },
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = u.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } ?: CircularProgressIndicator()
                }

                Spacer(modifier = Modifier.height(80.dp))

                val anyPermanentlyDenied = locationPermissionHandler.statuses.any { it.value == PermissionStatus.PermanentlyDenied }
                val anyDeniedOrUnknown = locationPermissionHandler.statuses.any { it.value == PermissionStatus.Denied || it.value == PermissionStatus.Unknown }
                val locationService = remember { LocationService(context) }

                if (anyPermanentlyDenied) {
                    Button(
                        onClick = {
                            // Porta l'utente nelle impostazioni dell'app
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Apri impostazioni per concedere i permessi")
                    }
                } else if (anyDeniedOrUnknown) {
                    Button(
                        onClick = { locationPermissionHandler.launchPermissionRequest() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Richiedi permessi di localizzazione")
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            CoroutineScope(Dispatchers.Main).launch {
                                val location = locationService.getCurrentLocation()
                                location?.let {
                                    Toast.makeText(
                                        context,
                                        "Posizione: %.4f, %.4f".format(it.latitude, it.longitude),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Mostra posizione attuale")
                    }
                }
            }

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Esci", color = MaterialTheme.colorScheme.onError)
            }
        }
    }
}




