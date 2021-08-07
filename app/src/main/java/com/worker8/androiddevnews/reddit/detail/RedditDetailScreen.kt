package com.worker8.androiddevnews.reddit.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kirkbushman.araw.models.Comment
import com.kirkbushman.araw.models.base.CommentData
import com.worker8.androiddevnews.reddit.HtmlViewEncode
import kotlinx.coroutines.cancel

@Composable
fun RedditDetailScreen(
    controller: RedditDetailController,
    state: MutableState<List<CommentData>>,
    submissionId: String
) {
    val scope = rememberCoroutineScope()
    DisposableEffect(scope) {
        controller.init(scope, state, submissionId)
        onDispose {
            scope.cancel()
        }
    }
    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxHeight()
    ) {
        items(
            count = state.value.count(),
            itemContent = { index ->
                val item = state.value[index]
                Column(modifier = Modifier.padding(start = 4.dp)) {
                    if (item is Comment) {
                        Text(
                            style = MaterialTheme.typography.subtitle2,
                            text = item.author
                        )
                        HtmlViewEncode(item.bodyHtml)
                    }
                }
            })
    }
}

