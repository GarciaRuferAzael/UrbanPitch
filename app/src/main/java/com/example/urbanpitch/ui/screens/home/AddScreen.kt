package com.example.urbanpitch.ui.screens.home

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.urbanpitch.data.database.Pitch
import com.example.urbanpitch.ui.BottomNavigationBar
import com.example.urbanpitch.ui.PitchesViewModel
import com.example.urbanpitch.ui.UrbanPitchRoute
import com.example.urbanpitch.utils.saveImageToInternalStorage
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(navController: NavController, pitchVm: PitchesViewModel = koinViewModel()) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        topBar = {
            TopAppBar(title = { Text("Aggiungi Campo") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (imageUri != null) {
                        val path = saveImageToInternalStorage(context, imageUri!!)
                        val pitch = Pitch(
                            name = name,
                            description = description,
                            city = city,
                            imageUrl = "file://$path",
                            latitude = latitude.toFloatOrNull() ?: 0f,
                            longitude = longitude.toFloatOrNull() ?: 0f
                        )
                        pitchVm.addPitch(pitch)
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, "Seleziona un'immagine", Toast.LENGTH_SHORT).show()
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome") })
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descrizione") })
            OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("Città") })
            OutlinedTextField(value = latitude, onValueChange = { latitude = it }, label = { Text("Latitudine") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = longitude, onValueChange = { longitude = it }, label = { Text("Longitudine") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

            Button(onClick = { pickImageLauncher.launch("image/*") }) {
                Text("Seleziona immagine")
            }

            imageUri?.let {
                AsyncImage(
                    model = it,
                    contentDescription = "Anteprima immagine",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
