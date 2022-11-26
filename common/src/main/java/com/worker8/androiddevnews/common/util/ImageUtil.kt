package com.worker8.androiddevnews.common.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import com.worker8.androiddevnews.common.R

@Composable
fun createImageRequest(imageUrl: String) = ImageRequest.Builder(LocalContext.current)
    .error(R.drawable.image_place_holder)
    .placeholder(R.drawable.image_place_holder)
    .data(imageUrl)
    .build()