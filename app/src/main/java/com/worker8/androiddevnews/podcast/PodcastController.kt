package com.worker8.androiddevnews.podcast

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import com.prof.rssparser.Channel
import com.prof.rssparser.Parser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject


class PodcastController @Inject constructor(private val rssParser: Parser) :
    PodcastContract.ViewState {
    override val state:State<Channel> get() = _state
     lateinit var _state:State<Channel>
    fun setInput(scope: CoroutineScope, initialState: MutableState<Channel>) {
        _state = initialState
        Log.d(
            "ccw",
            "[$this] initial state[${System.identityHashCode(initialState)}]: $initialState"
        )
        val url = "https://feeds.simplecast.com/LpAGSLnY"
        scope.launch {
//            state.emit(initialState.value)
            try {
                Log.d("ddw","1 before getChannel")
                val channel = rssParser.getChannel(url)
                Log.d("ddw","2 after getChannel")
//                Log.d("ddw", "channel: $channel")
                initialState.value = channel
                Log.d("ddw","3 after emit")
                // Do something with your data
            } catch (e: Exception) {
                Log.d("ddw","4 ERROR")
                e.printStackTrace()
                // Handle the exception
            }
        }

    }
}