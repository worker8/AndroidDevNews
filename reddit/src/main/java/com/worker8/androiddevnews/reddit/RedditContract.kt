package com.worker8.androiddevnews.reddit

import androidx.compose.runtime.State
import com.kirkbushman.araw.models.Submission

class RedditContract {
    interface ViewState {
        val state: State<List<Submission>>
    }
}