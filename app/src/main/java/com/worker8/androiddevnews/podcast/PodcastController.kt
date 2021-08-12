package com.worker8.androiddevnews.podcast

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import com.icosillion.podengine.models.Podcast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL
import javax.inject.Inject


class PodcastController @Inject constructor() :
    PodcastContract.ViewState {
    override val state: State<Podcast?> get() = _state
    private lateinit var _state: MutableState<Podcast?>
    fun setInput(scope: CoroutineScope, initialState: MutableState<Podcast?>) {
        _state = initialState
        Log.d(
            "ccw",
            "[$this] initial state[${System.identityHashCode(initialState)}]: $initialState"
        )
//        val url = "https://feeds.simplecast.com/LpAGSLnY"
//        val url = "https://blog.jetbrains.com/feed/"
//        val url = "https://fragmentedpodcast.com/feed/"
        scope.launch((Dispatchers.IO)) {
            try {
                val podcast = Podcast(URL("https://feeds.simplecast.com/LpAGSLnY"))
//                podcast.print()
//                podcast.episodes.forEach { episode ->
//                    episode.print()
//                }
                _state.value = podcast
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}