package com.worker8.androiddevnews.ui

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.worker8.androiddevnews.util.getActivity
import java.net.URL

fun createWebView(
    context: Context,
    linkUrl: String,
    shouldRedirectInSamePage: Boolean,
    onPageFinished: ((String) -> Unit)? = null
) =
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
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                view?.title?.let { onPageFinished?.invoke(it) }
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                url: String?
            ): Boolean {
                if (shouldRedirectInSamePage) {
                    url?.let {
                        view?.loadUrl(it)
                    }
                } else {
                    if (url.isNullOrBlank()) {
                        return false
                    }
                    val intent = Intent(context, WebViewActivity::class.java).apply {
                        putExtra(WebViewActivity.UrlKey, url)
                    }
                    context.startActivity(intent)
                }
                return true
            }
        }
        loadUrl(linkUrl)
    }

@Composable
fun WebViewScreen(linkUrl: String) {
    val pageTitle = remember { mutableStateOf("") }
    val activity = LocalContext.current.getActivity()
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        TopAppBar(
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.onBackground
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = null,
                modifier = Modifier
                    .width(32.dp)
                    .height(32.dp)
                    .padding(6.dp)
                    .padding(start = 4.dp)
                    .clickable {
                        activity?.finish()
                    }
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = URL(linkUrl).host,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
                if (pageTitle.value.isNotBlank()) {
                    Text(
                        text = pageTitle.value.uppercase(),
                        modifier = Modifier
                            .alpha(0.8f)
                            .padding(bottom = 2.dp, end = 16.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        Surface(color = MaterialTheme.colors.background) {
            AndroidView(factory = { context ->
                createWebView(context, linkUrl, true, onPageFinished = {
                    pageTitle.value = it
                })
            })
        }
    }
}