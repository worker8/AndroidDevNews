package com.worker8.androiddevnews.newsletter

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.worker8.androiddevnews.ui.createWebView

private const val LatestAndroidWeeklyUrl = "https://androidweekly.net/#latest-issue"

@Composable
fun AndroidWeeklyScreen() {
    val backgroundColor = MaterialTheme.colors.background
    AndroidView(factory = { context ->
        createWebView(
            context = context,
            linkUrl = LatestAndroidWeeklyUrl,
            backgroundColor = backgroundColor
        )
    })
}
