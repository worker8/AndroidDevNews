package com.worker8.androiddevnews.podcast

import android.util.Log
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
//        exoPlayer.addListener(object : Player.Listener {
//            override fun onIsPlayingChanged(isPlaying: Boolean) {
//                super.onIsPlayingChanged(isPlaying)
//                viewState.isPlaying.value = isPlaying
//            }
//        })
        input.isPlaying
            .onEach { viewState.isPlaying.value = it }
            .launchIn(scope)
        input.update
            .onEach {
                Log.d("ccw", "[controller] input.update")
                viewState.progress.value = it.progress
                viewState.currentPlaying.value = it
            }
            .launchIn(scope)
        input.listPlayClick
            .filter {
                viewState.currentPlayingEpisode.value?.guid != it.guid
            }
            .onEach { viewState.currentPlayingEpisode.value = it }
            .onEach { _episode ->
                input.startServiceCallback(
                    _episode.title,
                    _episode.iTunesInfo.summary.take(50),
                    _episode.enclosure.url.toString(),
                    viewState.podcast.value?.imageURL.toString()
                )
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
//                    viewState.podcast.value = Podcast(URL("https://adbackstage.libsyn.com/rss"))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.launchIn(scope)
    }
}