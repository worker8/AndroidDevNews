package com.worker8.androiddevnews.podcast

import android.util.Log
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.icosillion.podengine.models.Podcast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.*
import javax.inject.Inject
import kotlin.time.ExperimentalTime


@ExperimentalMaterialApi
class PodcastController @Inject constructor() {
    var exoPlayer: ExoPlayer? = null

    @ExperimentalTime
    fun setInput(
        scope: CoroutineScope,
        input: PodcastContract.Input,
        viewState: PodcastContract.ViewState,
    ) {
//        val url = "https://feeds.simplecast.com/LpAGSLnY"
//        val url = "https://blog.jetbrains.com/feed/"
//        val url = "https://fragmentedpodcast.com/feed/"

        input.exoPlayer.onEach {
            exoPlayer = it
            it.addListener(object : Player.Listener {
                override fun onIsLoadingChanged(isLoading: Boolean) {
                    super.onIsLoadingChanged(isLoading)
                    Log.d("ddw", "onIsPlayingChanged.isLoading: $isLoading")
                    viewState.isLoading.value = isLoading
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    Log.d("ddw", "onIsPlayingChanged.isPlaying: $isPlaying")
                    viewState.isPlaying.value = isPlaying
                }
            })
        }.launchIn(scope)

        input.update
            .onEach {
                Log.d("ccw", "[controller] input.update")
                viewState.progress.value = it.progress
                viewState.currentPlaying.value = it
            }
            .launchIn(scope)
        input.listPlayClick
            .filter {
                // if different episode is clicked, we start the service
                viewState.currentPlayingEpisode.value?.episode?.guid != it.episode.guid
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
            }.launchIn(scope)
        input.listPlayClick.filter {
            // if same episode is clicked
            viewState.currentPlayingEpisode.value?.episode?.guid == it.episode.guid
        }
            .onEach {
                Log.d("ddw", "the same episode is clicked")
                exoPlayer?.apply {
                    if (isPlaying) {
                        pause()
                    } else if (!isLoading) {
                        playWhenReady = true
                    }
                }
            }
            .launchIn(scope)
        input.listPlayClick
            .onEach {
                Log.d("ddw", "listPlayClick - show bottom sheet")
                viewState.bottomSheetControlIsOpen.value = ModalBottomSheetValue.Expanded
            }
            .launchIn(scope)
        flow<Unit> {
            withContext(Dispatchers.IO) {
                runCatching {
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
                }
            }
        }.launchIn(scope)
    }
}