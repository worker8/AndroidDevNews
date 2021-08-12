package com.worker8.androiddevnews.podcast

import androidx.compose.runtime.State
import com.icosillion.podengine.models.Podcast

class PodcastContract {
    interface ViewState {
        val state: State<Podcast?>
    }
}