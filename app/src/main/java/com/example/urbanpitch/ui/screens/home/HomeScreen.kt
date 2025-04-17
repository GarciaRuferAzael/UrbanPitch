package com.example.urbanpitch.ui.screens.home

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.urbanpitch.data.database.Pitch
import com.example.urbanpitch.ui.PitchesState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(state: PitchesState, navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Campi Sportivi") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("addPitch")
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
                    PitchItem(item, onClick = {})
                }
            }
        } else {
            Text("nessun item trovato")
        }
    }
}

@Composable
fun PitchItem(pitch: Pitch, onClick: () -> Unit) {
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = pitch.name)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = pitch.city)
            Text(text = pitch.description)
        }
    }
}
