package com.worker8.androiddevnews.reddit

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import com.kirkbushman.araw.models.Submission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class RedditController @Inject constructor(var redditRepo: RedditRepo) : RedditContract.ViewState {
    override val state: State<List<Submission>> get() = _state
    lateinit var _state: State<List<Submission>>
    fun setInput(scope: CoroutineScope, initialState: MutableState<List<Submission>>) {
        _state = initialState
        redditRepo.getRedditClient()
            .onEach { initialState.value = redditRepo.getSubmission() }
            .catch { it.printStackTrace() }
            .flowOn(Dispatchers.IO)
            .launchIn(scope)
    }
}