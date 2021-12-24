package com.worker8.androiddevnews.newsletter

import android.util.Log
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.prof.rssparser.Channel
import com.prof.rssparser.Parser
import com.worker8.androiddevnews.ui.createWebView
import java.nio.charset.Charset

@Composable
fun KotlinWeeklyScreen() {
    val kotlinWeeklyUrl =
        "https://us12.campaign-archive.com/feed?u=f39692e245b94f7fb693b6d82&id=93b2272cb6"
    val parser = Parser.Builder()
        .context(LocalContext.current)
        .charset(Charset.forName("ISO-8859-7"))
        .cacheExpirationMillis(24L * 60L * 60L * 100L) // one day
        .build()
    val latestIssueLink = remember { mutableStateOf<String?>(null) }
    latestIssueLink.value?.let {

    }

    if (latestIssueLink.value != null) {
        AndroidView(factory = { context ->
            createWebView(context, latestIssueLink.value!!, false)
        })
    } else {
        CircularProgressIndicator()
    }

    LaunchedEffect(Unit) {
        try {
            val channel: Channel = parser.getChannel(kotlinWeeklyUrl)
            latestIssueLink.value = channel.articles.first().link
            // Do something with your data
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle the exception
        }
    }
}