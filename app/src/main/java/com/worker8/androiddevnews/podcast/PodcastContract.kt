package com.worker8.androiddevnews.podcast

import androidx.compose.runtime.State
import com.prof.rssparser.Channel

class PodcastContract {
    interface ViewState {
        val state: State<Channel>
    }
}