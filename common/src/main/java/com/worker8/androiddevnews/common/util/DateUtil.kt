package com.worker8.androiddevnews.common.util

import android.text.format.DateUtils
import java.util.Date
import java.util.Locale

fun Date.toRelativeTimeString() =
    DateUtils
        .getRelativeTimeSpanString(time, Date().time, DateUtils.MINUTE_IN_MILLIS)
        .toString()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
