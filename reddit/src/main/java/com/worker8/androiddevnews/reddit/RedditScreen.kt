package com.worker8.androiddevnews.reddit

import android.content.Intent
import android.webkit.MimeTypeMap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.kirkbushman.araw.models.Submission
import com.worker8.androiddevnews.common.compose.theme.Neutral02
import com.worker8.androiddevnews.reddit.detail.RedditDetailActivity
import com.worker8.androiddevnews.reddit.detail.RedditDetailActivity.Companion.SubmissionKey
import com.worker8.androiddevnews.reddit.shared.RedditContentCard
import kotlinx.coroutines.cancel

@Composable
fun RedditScreen(
    navController: NavHostController,
    controller: RedditController,
    state: MutableState<List<Submission>>,
    lazyListState: LazyListState,
    onCardClick: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    DisposableEffect(scope) {
        controller.setInput(scope, state)
        onDispose {
            scope.cancel()
        }
    }
    RedditList(navController, state, lazyListState, onCardClick)
}

@Composable
fun RedditList(
    navController: NavHostController,
    state: State<List<Submission>>,
    lazyListState: LazyListState,
    onCardClick: (String) -> Unit,
) {
    val context = LocalContext.current
//    CompositionLocalProvider(LocalImageLoader provides ImageLoader(LocalContext.current)) {
    if (state.value.isEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .semantics { testTag = "AAABBBB" }
                .background(MaterialTheme.colors.background)
                .fillMaxHeight(),
            state = lazyListState
        ) {
            items(
                count = state.value.count(),
                key = { index -> state.value[index].id },
                itemContent = { index ->
                    val submission = state.value[index]

                    RedditContentCard(
                        submission = submission,
                        truncation = true,
                        onLinkClick = { _submission ->
                            onCardClick(_submission.url)
                        },
                        onClick = {
                            val intent = Intent(context, RedditDetailActivity::class.java).apply {
                                putExtra(SubmissionKey, submission)
                            }
                            context.startActivity(intent)
                        })
                    Divider(
                        color = MaterialTheme.colors.Neutral02,
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            )
        }
    }
}

@Composable
fun RenderFlair(text: String) {
    Card(
        Modifier.padding(start = 4.dp),
        shape = RoundedCornerShape(10),
        backgroundColor = MaterialTheme.colors.Neutral02,
    ) {
        Text(
            style = MaterialTheme.typography.caption.copy(
                color = MaterialTheme.colors.onBackground
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            text = text
        )
    }

}

fun String.isImageUrl(): Boolean {
    val fileExtension = MimeTypeMap.getFileExtensionFromUrl(this)
    return fileExtension.endsWith(".png")
            || fileExtension.endsWith(".jpeg")
            || fileExtension.endsWith(".jpg")
}