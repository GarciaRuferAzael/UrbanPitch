package com.example.urbanpitch.ui.composables

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

enum class Size { Sm, Lg }

@Composable
fun ImageWithPlaceholder(uri: Uri?,size: Size, modifier: Modifier = Modifier) {
    val imageModifier = modifier
        .fillMaxWidth()
        .height(360.dp)
        .clip(RoundedCornerShape(12.dp))

    if (uri?.path?.isNotEmpty() == true) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .crossfade(true)
                .build(),
            contentDescription = "Pitch picture",
            contentScale = ContentScale.Crop,
            modifier = imageModifier
        )
    } else {
        Box(
            modifier = imageModifier
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Image,
                contentDescription = "Placeholder image",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun ImageGroup(uri: Uri?, size: Size, modifier: Modifier = Modifier) {
    val imageSize = if (size == Size.Sm) 72.dp else 128.dp
    if (uri?.path?.isNotEmpty() == true) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .crossfade(true)
                .build(),
            contentDescription = "Pitch picture",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(imageSize)
                .clip(RoundedCornerShape(8.dp))
        )
    } else {
        Image(
            imageVector = Icons.Outlined.Image,
            contentDescription = "Pitch picture placeholder",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
            modifier = modifier
                .size(imageSize)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(16.dp)
        )
    }
}

@Composable
fun ImageGallery(uris: List<Uri?>, size: Size = Size.Sm) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        uris.take(4).forEach { uri ->
            ImageGroup(uri = uri, size = size)
        }
    }
}

