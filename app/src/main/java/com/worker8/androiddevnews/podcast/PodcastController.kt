package com.worker8.androiddevnews.podcast

import androidx.compose.material.ExperimentalMaterialApi
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.ExperimentalTime


@ExperimentalMaterialApi
class PodcastController @Inject constructor(
    private val podcastRepository: PodcastRepository
) {
    var exoPlayer: ExoPlayer? = null

    @OptIn(ExperimentalTime::class)
    fun setInput(
        scope: CoroutineScope,
        input: PodcastContract.Input,
        viewState: PodcastContract.ViewState,
    ) {
        input.exoPlayer.onEach {
            exoPlayer = it
            it.addListener(object : Player.Listener {
                override fun onIsLoadingChanged(isLoading: Boolean) {
                    super.onIsLoadingChanged(isLoading)
                    viewState.isLoading.value = isLoading
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    viewState.isPlaying.value = isPlaying
                }
            })
        }.launchIn(scope)

        input.update
            .onEach {
                viewState.progress.value = it.progress
                viewState.currentPlaying.value = it
            }
            .launchIn(scope)

        // if different episode is clicked, we start the service
        input.listPlayClick
            .filter {
                isSameEpisodeClicked(
                    currentEpisode = viewState.currentPlayingEpisode.value,
                    clickedEpisode = it
                ).not()
            }
            .onEach { _episodePair ->
                val episode = _episodePair.episode
                input.startServiceCallback(
                    episode.title,
                    episode.iTunesInfo.summary?.take(50) ?: "",
                    episode.enclosure.url.toString(),
                    _episodePair.podcastImageUrl
                )
                viewState.currentPlayingEpisode.value = _episodePair
            }
            .launchIn(scope)

        // if same episode is clicked
        input.listPlayClick
            .filter {
                isSameEpisodeClicked(
                    currentEpisode = viewState.currentPlayingEpisode.value,
                    clickedEpisode = it
                )
            }
            .onEach {
                exoPlayer?.apply {
                    if (isPlaying) {
                        pause()
                    } else if (!isLoading) {
                        playWhenReady = true
                    }
                }
            }
            .launchIn(scope)

        scope.launch(Dispatchers.IO) {
            viewState.episodePairs.value = podcastRepository.getAllPodcasts().toList()
        }
    }

    @ExperimentalTime
    private fun isSameEpisodeClicked(
        currentEpisode: PodcastContract.EpisodePair?,
        clickedEpisode: PodcastContract.EpisodePair
    ): Boolean {
        return currentEpisode?.episode?.guid == clickedEpisode.episode.guid
    }

}