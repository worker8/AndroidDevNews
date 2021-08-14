package com.worker8.androiddevnews.podcast.model

import android.util.Log
import com.icosillion.podengine.models.Episode
import com.icosillion.podengine.models.Podcast


fun Podcast.print() {
    val sb = StringBuilder()
    sb.appendLine("--> ---Podcast---")
    sb.appendLine("--> title: $title")
    sb.appendLine("--> description: $description")
    sb.appendLine("--> link: $link")
    sb.appendLine("--> pubDate: $pubDate")
    Log.d("ddw", "$sb")
}

fun Episode.print() {
    val sb = StringBuilder()
    sb.appendLine("--> ---Episode---")
    sb.appendLine("--> title: $title")
    sb.appendLine("--> description: $description")
    sb.appendLine("--> link: $link")
    sb.appendLine("--> pubDate: $pubDate")
    sb.appendLine("--> iTunesInfo.title: ${this.iTunesInfo.title}")
    sb.appendLine("--> iTunesInfo.duration: ${this.iTunesInfo.duration}")
    sb.appendLine("--> iTunesInfo.episodeNumber: ${this.iTunesInfo.episodeNumber}")
    Log.d("ddw", "$sb")
}