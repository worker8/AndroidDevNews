package com.worker8.androiddevnews.reddit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kirkbushman.araw.models.Submission
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class RedditViewModel @Inject constructor(var redditRepo: RedditRepo) : Contract.ViewState,
    ViewModel() {
    override val state = MutableStateFlow<List<Submission>>(listOf())
    fun setInput() {
        redditRepo.getRedditClient()
            .onEach { state.emit(redditRepo.getSubmission()) }
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