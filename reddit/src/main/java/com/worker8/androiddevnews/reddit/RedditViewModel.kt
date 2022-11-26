package com.worker8.androiddevnews.reddit

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kirkbushman.araw.models.Submission
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class RedditViewModel @Inject constructor(var redditRepo: RedditRepo) : RedditContract.ViewState,
    ViewModel() {
    override val state = mutableStateOf(listOf<Submission>())
    fun setInput() {
        redditRepo.getRedditClient()
            .onEach { state.value = redditRepo.getSubmission() }
//            .map { submissions ->
//                val debugStringBuilder = StringBuilder()
//                submissions!!.map { it.copy(selfText = "(hidden)", selfTextHtml = "(hidden)") }
//                    .forEachIndexed { index, submission ->
//                        val fields = submission.toString().split(",")
//                        fields.forEach {
//                            debugStringBuilder.appendLine("#$index:  $it")
//                        }
//                        debugStringBuilder.appendLine("-------")
//
//                        Log.d(
//                            "ddw2",
//                            "#$index] domain: ${submission.domain}, ${submission.media}"
//                        )
//                    }
//                Log.d("ddw2", debugStringBuilder.toString())
//            }
            .catch { it.printStackTrace() }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }
}