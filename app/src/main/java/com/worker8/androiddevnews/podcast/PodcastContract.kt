package com.worker8.androiddevnews.podcast

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import com.icosillion.podengine.models.Episode
import com.icosillion.podengine.models.Podcast
import kotlinx.coroutines.flow.MutableSharedFlow

class PodcastContract {
    interface Input {
        val listPlayClick: MutableSharedFlow<Episode>
        val controlPlayClick: MutableSharedFlow<Unit>
        val progress: MutableSharedFlow<Float>
        val startServiceCallback: (String, String, String) -> Unit
    }

    interface ViewState {
        val podcast: MutableState<Podcast?>
        val progress: MutableState<Float>
        val currentPlayingEpisode: MutableState<Episode?>
        val isPlaying: MutableState<Boolean>
        val lazyListState: LazyListState
    }
}