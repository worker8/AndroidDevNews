package com.worker8.androiddevnews.newsletter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.prof.rssparser.Parser
import com.worker8.androiddevnews.ui.createWebView
import java.nio.charset.Charset

@Composable
fun KotlinWeeklyScreen() {
    val kotlinWeeklyRss =
        "https://us12.campaign-archive.com/feed?u=f39692e245b94f7fb693b6d82&id=93b2272cb6"
    val parser = Parser.Builder()
        .context(LocalContext.current)
        .charset(Charset.forName("ISO-8859-7"))
        .cacheExpirationMillis(24L * 60L * 60L * 100L) // one day
        .build()
    val latestIssueLink = remember { mutableStateOf<String?>(null) }
    if (latestIssueLink.value != null) {
        val backgroundColor = MaterialTheme.colors.background
        AndroidView(factory = { context ->
            createWebView(
                context = context,
                linkUrl = latestIssueLink.value!!,
                backgroundColor = backgroundColor
            )
        })
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
    }

    LaunchedEffect(Unit) {
        try {
            val channel = parser.getChannel(kotlinWeeklyRss)
            latestIssueLink.value = channel.articles.first().link
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}