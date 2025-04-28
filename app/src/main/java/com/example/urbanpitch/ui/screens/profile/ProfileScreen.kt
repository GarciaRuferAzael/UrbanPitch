package com.example.urbanpitch.ui.screens.profile

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.urbanpitch.ui.composables.BottomNavigationBar
import com.example.urbanpitch.ui.PitchesState
import com.example.urbanpitch.ui.UrbanPitchRoute
import com.example.urbanpitch.ui.composables.AppBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(state: PitchesState, navController: NavController) {
    Scaffold(
        topBar = { AppBar(navController, title = "Profilo Utente") },
        bottomBar = { BottomNavigationBar(navController) },
        /*floatingActionButton = {
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
        }*/
    ) { contentPadding ->
        Box(modifier = Modifier.padding(contentPadding)) {

        }
    }
}