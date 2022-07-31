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
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.imageLoader
import com.worker8.androiddevnews.util.createImageRequest

@Composable
fun ImageAspectRatio(
    imageUrl: String,
    contentScale: ContentScale = ContentScale.Fit,
    modifier: Modifier = Modifier
) {
    val (aspectRatio, setAspectRatio) = remember { mutableStateOf(1f) }
    AsyncImage(
        model = imageUrl,
//        painter = rememberAsyncImagePainter(
//            createImageRequest(imageUrl),
//            LocalContext.current.imageLoader
//        ) { _, current ->
//            setAspectRatio(current.size.width / current.size.height)
//            true
//        },
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio, true)
    )
}