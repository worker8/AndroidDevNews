package com.worker8.androiddevnews.podcast

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.MutableState
import com.google.android.exoplayer2.ExoPlayer
import com.icosillion.podengine.models.Episode
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalTime
class PodcastContract {
    interface Input {
        val listPlayClick: MutableSharedFlow<EpisodePair>
        val isPlaying: MutableSharedFlow<Boolean>
        val controlPlayClick: MutableSharedFlow<Unit>
        val update: MutableSharedFlow<PodcastService.CurrentProgress>
        val startServiceCallback: (String, String, String, String) -> Unit
        val exoPlayer: MutableSharedFlow<ExoPlayer>
    }

    interface ViewState {
        val episodePairs: MutableState<List<EpisodePair>>
        val progress: MutableState<Float>

        // TODO: naming is bad
        val currentPlaying: MutableState<PodcastService.CurrentProgress?>
        val currentPlayingEpisode: MutableState<EpisodePair?>
        val isPlaying: MutableState<Boolean>
        val lazyListState: LazyListState
        val bottomSheetControlIsOpen: MutableState<ModalBottomSheetValue>
    }

    data class EpisodePair(
        val episode: Episode,
        val podcastImageUrl: String,
        val podcastTitle: String
    )
}