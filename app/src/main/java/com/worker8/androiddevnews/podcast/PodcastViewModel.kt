package com.worker8.androiddevnews.podcast

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prof.rssparser.Channel
import com.prof.rssparser.Parser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PodcastViewModel @Inject constructor(private val rssParser: Parser) :
    PodcastContract.ViewState, ViewModel() {
    override val state = mutableStateOf(Channel())
    fun setInput() {
        val url = "https://feeds.simplecast.com/LpAGSLnY"
        viewModelScope.launch {
            try {
                val channel = rssParser.getChannel(url)
//                Log.d("ddw", "channel: $channel")
                // state.emit(channel)
                // Do something with your data
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the exception
            }
        }

    }
}