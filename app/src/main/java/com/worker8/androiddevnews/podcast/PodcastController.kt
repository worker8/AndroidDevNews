package com.worker8.androiddevnews.podcast

import android.util.Log
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.icosillion.podengine.models.Podcast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject
import kotlin.time.ExperimentalTime


class PodcastController @Inject constructor() {
    @ExperimentalTime
    fun setInput(
        scope: CoroutineScope,
        input: PodcastContract.Input,
        viewState: PodcastContract.ViewState,
        exoPlayer: SimpleExoPlayer
    ) {
//        val url = "https://feeds.simplecast.com/LpAGSLnY"
//        val url = "https://blog.jetbrains.com/feed/"
//        val url = "https://fragmentedpodcast.com/feed/"
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                viewState.isPlaying.value = isPlaying
            }
        })
        input.progress
            .onEach {
                viewState.progress.value = it
            }
            .launchIn(scope)
        input.listPlayClick
            .filter {
                viewState.currentPlayingEpisode.value?.guid != it.guid
            }
            .onEach { viewState.currentPlayingEpisode.value = it }
            .map { it.enclosure.url.toString() }
            .onEach {
                viewState.currentPlayingEpisode?.component1()?.apply {
                    input.startServiceCallback(title, iTunesInfo.summary.take(50), it)
                }
            }.launchIn(scope)
        merge(input.controlPlayClick,
            input.listPlayClick
                .filter {
                    viewState.currentPlayingEpisode.value?.guid == it.guid
                })
            .onEach {
                Log.d("ddw", "Controller - control CLCIK")
            }
            .launchIn(scope)
        flow<Unit> {
            withContext(Dispatchers.IO) {
                try {
                    viewState.podcast.value = Podcast(URL("https://feeds.simplecast.com/LpAGSLnY"))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.launchIn(scope)
    }
}