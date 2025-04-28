package com.example.urbanpitch.ui.screens.details

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.urbanpitch.data.database.Pitch
import com.example.urbanpitch.ui.composables.AppBar
import com.example.urbanpitch.ui.composables.ImageGallery
import com.example.urbanpitch.ui.composables.ImageWithPlaceholder
import com.example.urbanpitch.ui.composables.Size

@Composable
fun DetailsScreen(pitch: Pitch, navController: NavController) {
    val ctx = LocalContext.current

    fun shareDetails() {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, pitch.name)
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Pitch")
        if (shareIntent.resolveActivity(ctx.packageManager) != null) {
            ctx.startActivity(shareIntent)
        }
    }

    Scaffold(
        topBar = { AppBar(navController, title = "Dettagli Campo") },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.tertiary,
                onClick = ::shareDetails
            ) {
                Icon(Icons.Outlined.Share, "Share Pitch")
            }
        },
    ) { contentPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(contentPadding).padding(12.dp).fillMaxSize()
        ) {
            val imageUri = Uri.parse(pitch.imageUrl)
            ImageWithPlaceholder(imageUri, Size.Lg, modifier = Modifier.padding(16.dp))
            Text(
                pitch.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                pitch.city,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.size(8.dp))
            Text(
                pitch.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
