package com.worker8.androiddevnews.reddit.detail

import androidx.compose.runtime.State
import com.kirkbushman.araw.models.base.CommentData

class RedditDetailContract {
    interface ViewState {
        val state: State<List<CommentData>>
    }
}