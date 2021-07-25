package com.worker8.androiddevnews.util

import android.text.format.DateUtils
import java.util.*

fun Date.toRelativeTimeString() =
    DateUtils
        .getRelativeTimeSpanString(time, Date().time, DateUtils.MINUTE_IN_MILLIS)
        .toString()
        .lowercase()
