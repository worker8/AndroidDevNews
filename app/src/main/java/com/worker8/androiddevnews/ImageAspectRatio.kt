package com.worker8.androiddevnews

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.util.DebugLogger

@Composable
fun ImageAspectRatio(
    imageUrl: String,
    contentScale: ContentScale = ContentScale.Fit,
    modifier: Modifier = Modifier
) {
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .logger(DebugLogger())
        .build()
    var (aspectRatio, setAspectRatio) = remember { mutableStateOf(1f) }
    val imageRequest = ImageRequest.Builder(LocalContext.current)
        .error(R.drawable.image_place_holder)
        .placeholder(R.drawable.image_place_holder)
        .data(imageUrl)
        .build()
    Image(
        painter = rememberImagePainter(
            imageRequest,
            imageLoader
        ) { _, current ->
            setAspectRatio(current.size.width / current.size.height)
            true
        },
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio, true)

    )
}