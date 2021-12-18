package com.worker8.androiddevnews.androidweekly

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.viewinterop.AndroidView
import com.worker8.androiddevnews.ui.createWebView
import com.worker8.androiddevnews.ui.theme.AndroidDevNewsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebViewActivity : AppCompatActivity() {
    private val linkUrl: String get() = intent.getStringExtra(UrlKey)!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidDevNewsTheme {
                // TODO: there's currently a bug to use SwipeToClose with AndroidView
                // vertical scroll doesn't work
//                SwipeToCloseBox(onCloseCallback = {
//                    finish()
//                    window.decorView.alpha = 0f
//                }) {
                Surface(color = MaterialTheme.colors.background) {
                    AndroidView(factory = { context ->
                        createWebView(context, linkUrl)
                    })
                }
//                }
            }
        }
    }

    companion object {
        const val UrlKey = "UrlKey"
    }
}

// TODO:
// 1. make app bar
//    a. x button
//    b. title(show domain) and subtitle (show title)
//    c. burger menu show refersh, share, open in browser request desktop
//    d. increase/decrease text option needed, cause page like this - https://kiranrao.in/blog/2021/12/03/jetpack-compose-slots/
//       (text is too small in this page - this page does not allow pinch zoom, and text zoom works well)
//    e. dark mode or not settings
// 2. handle darkmode according to theme
// 3. show loading progress bar below app bar
// X. handle webview.settings for main AndroidWeekly Screen