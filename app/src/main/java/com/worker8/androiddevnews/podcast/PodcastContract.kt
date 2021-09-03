package com.worker8.androiddevnews.podcast

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import com.icosillion.podengine.models.Episode
import com.icosillion.podengine.models.Podcast
import kotlinx.coroutines.flow.MutableSharedFlow

class PodcastContract {
    interface Input {
        val listPlayClick: MutableSharedFlow<Episode>
        val isPlaying: MutableSharedFlow<Boolean>
        val controlPlayClick: MutableSharedFlow<Unit>
        val update: MutableSharedFlow<PodcastService.CurrentProgress>
        val startServiceCallback: (String, String, String, String) -> Unit
    }

    interface ViewState {
        val podcast: MutableState<Podcast?>
        val progress: MutableState<Float>
        // TODO: naming is bad
        val currentPlaying: MutableState<PodcastService.CurrentProgress?>
        val currentPlayingEpisode: MutableState<Episode?>
        val isPlaying: MutableState<Boolean>
        val lazyListState: LazyListState
    }
}