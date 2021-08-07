package com.worker8.androiddevnews.reddit.detail

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import com.kirkbushman.araw.models.base.CommentData
import com.worker8.androiddevnews.reddit.RedditRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class RedditDetailController @Inject constructor(private val redditRepo: RedditRepo) :
    RedditDetailContract.ViewState {
    override val state: State<List<CommentData>> get() = _state
    private lateinit var _state: State<List<CommentData>>
    fun init(
        scope: CoroutineScope,
        mutableState: MutableState<List<CommentData>>,
        submissionId: String
    ) {
        _state = mutableState
        redditRepo.getRedditClient()
            .onEach {
                val commentTree = redditRepo.getComment(submissionId)
                val result = mutableListOf<CommentData>()
                commentTree?.forEach { _commentData ->
                    traverseComments(_commentData, result)
                }
                mutableState.value = result.toList()
            }
            .catch { it.printStackTrace() }
            .flowOn(Dispatchers.IO)
            .launchIn(scope)
    }

    private fun traverseComments(root: CommentData, result: MutableList<CommentData>) {
        result.add(root)
        root.replies?.forEach {
            traverseComments(it, result)
        }
    }
}