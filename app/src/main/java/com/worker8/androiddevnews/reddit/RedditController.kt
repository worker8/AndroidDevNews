package com.worker8.androiddevnews.reddit

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import com.kirkbushman.araw.models.Comment
import com.kirkbushman.araw.models.MoreComments
import com.kirkbushman.araw.models.Submission
import com.kirkbushman.araw.models.base.CommentData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class RedditController @Inject constructor(val redditRepo: RedditRepo) : RedditContract.ViewState {
    override val state: State<List<Submission>> get() = _state
    private lateinit var _state: State<List<Submission>>
    fun setInput(scope: CoroutineScope, mutableState: MutableState<List<Submission>>) {
        _state = mutableState
        redditRepo.getRedditClient()
            .onEach {
            }
            .onEach { mutableState.value = redditRepo.getSubmission() }
            .onEach {
                val temp = it.contributionsClient.comments(state.value.first().id)
                val commentDataList = temp.fetchNext()
                val result = mutableListOf<CommentData>()
                commentDataList?.forEach { _commentData ->
                    traverseComments(_commentData, result)
                }
                result.forEach { _commentData ->
                    Log.d("ccw", "${_commentData.print()}")
                }
//                Log.d(
//                    "ccw", "comments.size: ${comments!!.size}"
//                )
//                commentDataList?.forEachIndexed { index, _commentData ->
//                    Log.d("ccw", "$index: ${_commentData.print()}")
//                }
            }
            .catch { it.printStackTrace() }
            .flowOn(Dispatchers.IO)
            .launchIn(scope)
        redditRepo.getRedditClient()
    }

    private fun traverseComments(root: CommentData, result: MutableList<CommentData>) {
        result.add(root)
        root.replies?.forEach {
            traverseComments(it, result)
        }
    }
}


fun CommentData.print(): String {
    return when (this) {
        is Comment -> {
            "depth(${depth}) ${this.body.take(100)}"
        }
        is MoreComments -> {
            "Load More (${this.repliesSize})"
        }
        else -> {
            "???"
        }
    }
}