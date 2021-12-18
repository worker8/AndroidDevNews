package com.worker8.androiddevnews.ui

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.worker8.androiddevnews.androidweekly.WebViewActivity

fun createWebView(context: Context, linkUrl: String) =
    WebView(context).apply {
        settings.apply {
            builtInZoomControls = true
            supportZoom()
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            databaseEnabled = true
            domStorageEnabled = true
            textZoom = 100
            setRenderPriority(WebSettings.RenderPriority.HIGH)
        }
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                url: String?
            ): Boolean {
                if (url.isNullOrBlank()) {
                    return false
                }
                val intent = Intent(context, WebViewActivity::class.java).apply {
                    putExtra(WebViewActivity.UrlKey, url)
                }
                context.startActivity(intent)
                return true
            }
        }
        loadUrl(linkUrl)
    }
