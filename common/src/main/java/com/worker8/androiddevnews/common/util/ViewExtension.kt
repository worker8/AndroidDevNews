package com.worker8.androiddevnews.common.util

import android.content.res.Resources

/**
 * @return pixel value
 */
fun Float.dpToPx(): Float {
    return Resources.getSystem().displayMetrics.density * this
}