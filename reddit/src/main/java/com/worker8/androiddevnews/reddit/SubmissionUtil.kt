package com.worker8.androiddevnews.reddit

import com.kirkbushman.araw.models.Submission
import kotlin.math.roundToInt


val Submission.upvoteRatioPercentage: String
    get() {
        return if (upvoteRatio != null) {
            "(${(upvoteRatio!! * 100).roundToInt()}%)"
        } else {
            ""
        }
    }