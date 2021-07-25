package com.worker8.androiddevnews.reddit

import com.kirkbushman.araw.RedditClient
import com.kirkbushman.araw.helpers.AuthUserlessHelper
import com.kirkbushman.araw.models.Submission
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RedditRepo @Inject constructor(
    val userlessAuth: AuthUserlessHelper
) {
    private var redditClient: RedditClient? = null
    fun getRedditClient() =
        flow {
            userlessAuth.getRedditClient()?.let {
                this@RedditRepo.redditClient = it
                emit(it)
            }
        }

    fun getSubmission(): List<Submission> {
        return redditClient
            ?.contributionsClient
            ?.submissions("androiddev", limit = 40L)
            ?.fetchNext()
            ?: error("redditClient has to be initialized first by calling getRedditClient()")
    }
}