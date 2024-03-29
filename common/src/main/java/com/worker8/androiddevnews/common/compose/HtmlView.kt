package com.worker8.androiddevnews.common.compose

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
import com.worker8.androiddevnews.common.util.dpToPx
import com.worker8.androiddevnews.common.util.trimTrailingWhitespace

@Composable
fun HtmlView(
    content: String,
    strip: Boolean = false,
    truncation: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val html = HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_MODE_LEGACY)
    // Remembers the HTML formatted description. Re-executes on a new description
    val strippedHtml = if (strip) {
        remember(content) {
            HtmlCompat.fromHtml(html.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    } else {
        html
    }

    val bulletColor = MaterialTheme.colors.onBackground.toArgb()

    // Remembers the HTML formatted description. Re-executes on a new description
    val styledHtml = remember(strippedHtml) {
        val builder =
            SpannableStringBuilder(trimTrailingWhitespace(strippedHtml))
        val bulletSpans =
            builder.getSpans(0, builder.length, BulletSpan::class.java)
        bulletSpans.forEach {
            val spanStart = builder.getSpanStart(it)
            val spanEnd = builder.getSpanEnd(it)
            builder.removeSpan(it)
            val bulletSpan = BulletSpan(8f.dpToPx().toInt(), bulletColor)
            builder.setSpan(
                bulletSpan,
                spanStart,
                spanEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        builder
    }

    // Displays the TextView on the screen and updates with the HTML description when inflated
    // Updates to htmlDescription will make AndroidView recompose and update the text
    val textLimit = 120
    val truncated = if (truncation && styledHtml.length > textLimit) {
        styledHtml.substring(0, textLimit) + "..."
    } else {
        styledHtml
    }
    AndroidView(
        factory = { context ->
            TextView(context).apply {
                movementMethod = LinkMovementMethod.getInstance()
            }
        },
        update = {
            it.text = truncated
            it.setOnClickListener { onClick?.invoke() }
        }
    )
}