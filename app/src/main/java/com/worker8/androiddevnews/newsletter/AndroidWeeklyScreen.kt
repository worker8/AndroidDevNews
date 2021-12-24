package com.worker8.androiddevnews.newsletter

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.worker8.androiddevnews.ui.createWebView

private const val LatestAndroidWeeklyUrl = "https://androidweekly.net/#latest-issue"

@Composable
fun AndroidWeeklyScreen() {
    AndroidView(factory = { context ->
        createWebView(context, LatestAndroidWeeklyUrl, false)
    })
}
