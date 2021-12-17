package com.worker8.androiddevnews.androidweekly

import android.content.Intent
import android.os.Build
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.worker8.androiddevnews.reddit.detail.RedditDetailActivity

private const val LatestAndroidWeeklyUrl = "https://androidweekly.net/#latest-issue"

@Composable
fun AndroidWeeklyScreen() {
    AndroidView(factory = { context ->
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    if (url.isNullOrBlank()) {
                        return false
                    }
                    val intent = Intent(context, AndroidWeeklyDetailActivity::class.java).apply {
                        putExtra(AndroidWeeklyDetailActivity.UrlKey, url)
                    }
                    context.startActivity(intent)
                    return true
                }
            }
            if (Build.VERSION.SDK_INT >= 29) {
                settings.apply {
                    forceDark = WebSettings.FORCE_DARK_AUTO
                }
            }
            loadUrl(LatestAndroidWeeklyUrl)
        }
    })
}
