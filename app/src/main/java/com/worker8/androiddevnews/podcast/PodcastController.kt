package com.worker8.androiddevnews.podcast

import android.util.Log
import com.google.android.exoplayer2.SimpleExoPlayer
import com.icosillion.podengine.models.Podcast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.*
import javax.inject.Inject
import kotlin.Comparator
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
                viewState.currentPlayingEpisode.value?.episode?.guid != it.episode.guid
            }
            .onEach { viewState.currentPlayingEpisode.value = it }
            .onEach { _episodePair ->
                val episode = _episodePair.episode
                input.startServiceCallback(
                    episode.title,
                    episode.iTunesInfo.summary?.take(50) ?: "",
                    episode.enclosure.url.toString(),
                    _episodePair.podcastImageUrl
                )
            }.launchIn(scope)
        merge(input.controlPlayClick,
            input.listPlayClick
                .filter {
                    viewState.currentPlayingEpisode.value?.episode?.guid == it.episode.guid
                })
            .onEach {
                Log.d("ddw", "Controller - control CLICK")
            }
            .launchIn(scope)
        flow<Unit> {
            withContext(Dispatchers.IO) {
                try {
                    val comparator =
                        Comparator<PodcastContract.EpisodePair> { a, b ->
                            if (a.episode.pubDate > b.episode.pubDate) {
                                -1
                            } else {
                                1
                            }
                        }
                    val treeSet = TreeSet(comparator)
                    val podcastList = listOf(
                        URL("https://feeds.simplecast.com/LpAGSLnY"), // fragmented podcast
                        URL("https://adbackstage.libsyn.com/rss"), // android backstage
                        URL("https://nowinandroid.libsyn.com/rss") // now in android
                    )
                    podcastList.forEach { url ->
                        val podcast = Podcast(url)
                        podcast.episodes.forEach {
                            treeSet.add(
                                PodcastContract.EpisodePair(
                                    it,
                                    podcast.imageURL.toString(),
                                    podcast.title
                                )
                            )
                        }
                    }
                    viewState.episodePairs.value = treeSet.toList()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.launchIn(scope)
    }
}