package com.example.urbanpitch.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import org.koin.androidx.compose.koinViewModel


@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = koinViewModel(), // Usa Koin
    onEditProfile: () -> Unit = {}
) {
    val user by authViewModel.currentUser.collectAsState()
    val context = LocalContext.current

    // Logout: torna al Login
    val onLogout = {
        authViewModel.logout()
        navController.navigate(UrbanPitchRoute.Login.toString()) {
            popUpTo(UrbanPitchRoute.Home.toString()) { inclusive = true }
        }
    }

    Scaffold(bottomBar = { BottomNavigationBar(navController) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
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
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            .padding(24.dp)
                    )
                }

                Text(u.username, style = MaterialTheme.typography.titleLarge)
                Text(u.email, style = MaterialTheme.typography.bodyMedium)
            } ?: run {
                CircularProgressIndicator()
            }

            OutlinedButton(onClick = onEditProfile) {
                Text("Edit Profile")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Log Out", color = MaterialTheme.colorScheme.onError)
            }
        }
    }
}


