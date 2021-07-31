package com.worker8.androiddevnews.reddit

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.BulletSpan
import android.widget.TextView
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.worker8.androiddevnews.util.dpToPx

@Composable
fun HtmlView(content: String) {
    val bulletColor = MaterialTheme.colors.onBackground.toArgb()

    // Remembers the HTML formatted description. Re-executes on a new description
    val htmlDescription = remember(content) {
        val html =
            SpannableStringBuilder(HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_MODE_LEGACY))
        val bulletSpans =
            html.getSpans(0, html.length, BulletSpan::class.java)
        bulletSpans.forEach {
            val spanStart = html.getSpanStart(it)
            val spanEnd = html.getSpanEnd(it)
            html.removeSpan(it)
            val bulletSpan = BulletSpan(8f.dpToPx().toInt(), bulletColor)
            html.setSpan(
                bulletSpan,
                spanStart,
                spanEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        html
    }

    // Displays the TextView on the screen and updates with the HTML description when inflated
    // Updates to htmlDescription will make AndroidView recompose and update the text
    AndroidView(
        factory = { context ->
            TextView(context).apply {
                movementMethod = LinkMovementMethod.getInstance()
                textSize = 16f
            }
        },
        update = {
            it.text = htmlDescription
        }
    )
}