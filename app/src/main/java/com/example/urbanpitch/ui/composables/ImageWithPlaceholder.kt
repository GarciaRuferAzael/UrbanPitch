package com.example.urbanpitch.ui.composables

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
fun ImageWithPlaceholder(uri: Uri?, size: Size) {
    if (uri?.path?.isNotEmpty() == true) {
        AsyncImage(
            ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .crossfade(true)
                .build(),
            "Pitch picture",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(if (size == Size.Sm) 72.dp else 128.dp)
                .clip(CircleShape)
        )
    } else {
        Image(
            Icons.Outlined.Info, //it has to be Image not Info
            "Pitch picture",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
            modifier = Modifier
                .size(if (size == Size.Sm) 72.dp else 128.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(if (size == Size.Sm) 20.dp else 36.dp)
        )
    }
}
