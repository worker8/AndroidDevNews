package com.worker8.androiddevnews.reddit

import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat

@Composable
fun HtmlView(content: String) {
    // Remembers the HTML formatted description. Re-executes on a new description
    val stripHtml = HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_MODE_COMPACT)
    val htmlDescription = remember(content) {
        HtmlCompat.fromHtml(stripHtml.toString(), HtmlCompat.FROM_HTML_MODE_COMPACT)
    }

    // Displays the TextView on the screen and updates with the HTML description when inflated
    // Updates to htmlDescription will make AndroidView recompose and update the text
    AndroidView(
        factory = { context ->
            TextView(context).apply {
                movementMethod = LinkMovementMethod.getInstance()
            }
        },
        update = {
            it.text = htmlDescription
        }
    )
}