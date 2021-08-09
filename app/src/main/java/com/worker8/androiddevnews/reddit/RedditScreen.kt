package com.worker8.androiddevnews.reddit

import android.content.Intent
import android.webkit.MimeTypeMap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.LocalImageLoader
import coil.util.DebugLogger
import com.kirkbushman.araw.models.Submission
import com.worker8.androiddevnews.reddit.detail.RedditDetailActivity
import com.worker8.androiddevnews.reddit.detail.RedditDetailActivity.Companion.SubmissionKey
import com.worker8.androiddevnews.reddit.shared.RedditContentCard
import com.worker8.androiddevnews.ui.theme.Neutral02
import kotlinx.coroutines.cancel

@Composable
fun RedditScreen(
    navController: NavHostController,
    controller: RedditController,
    state: MutableState<List<Submission>>,
    lazyListState: LazyListState
) {
    val scope = rememberCoroutineScope()
//    val state: MutableState<List<Submission>> = rememberSaveable { mutableStateOf(listOf<Submission>()) }
    DisposableEffect(scope) {
        controller.setInput(scope, state)
        onDispose {
            scope.cancel()
        }
    }
    RedditList(navController, state, lazyListState)
}

@Composable
fun RedditList(
    navController: NavHostController,
    state: State<List<Submission>>,
    lazyListState: LazyListState
) {
    val imageLoader =
        ImageLoader.Builder(LocalContext.current)
            .logger(DebugLogger())
            .build()
    val context = LocalContext.current
    CompositionLocalProvider(LocalImageLoader provides ImageLoader(LocalContext.current)) {
        LazyColumn(
            modifier = Modifier
                .semantics { testTag = "AAABBBB" }
                .background(MaterialTheme.colors.background)
                .fillMaxHeight(),
            state = lazyListState
        ) {
            items(
                count = state.value.count(),
                itemContent = { index ->
                    val submission = state.value[index]
//                    val upvoteRatio = if (submission.upvoteRatio != null) {
//                        "(${submission.upvoteRatio!! * 100}%)"
//                    } else {
//                        ""
//                    }
//                    val context = LocalContext.current
//                    Column(modifier = Modifier
//                        .padding(16.dp)
//                        .clickable {
//                            context.startActivity(
//                                Intent(context, RedditDetailActivity::class.java).apply {
//                                    putExtra(SubmissionKey, submission)
//                                })
////                            navController.navigate("reddit_detail/${submission.id}")
//                        }) {
//                        Text(
//                            text = index.toString() + submission.title,
//                            style = MaterialTheme.typography.h6
//                        )
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            Text(
//                                style = MaterialTheme.typography.subtitle2.copy(color = MaterialTheme.colors.Primary07),
//                                text = submission.author
//                            )
//                            Text(
//                                style = MaterialTheme.typography.caption,
//                                text = submission.createdDate.toRelativeTimeString(),
//                                modifier = Modifier.padding(start = 8.dp)
//                            )
//                            submission.linkFlairText?.let { RenderFlair(it) }
//                        }
//                        val imageUrl = if (submission.url.isImageUrl()) {
//                            submission.url
//                        } else {
//                            submission.preview?.source()?.url?.let {
//                                HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_COMPACT)
//                                    .toString()
//                            } ?: ""
//                        }
//                        if (imageUrl.isNotBlank()) {
//                            val height = submission.preview?.source()?.height ?: -1
//                            val width = submission.preview?.source()?.width ?: -1
//                            if (height != -1 && width != -1 && height > width) {
//                                // TODO: scale image properly, re-enable this text for debugging
////                                Text("PORTRAIT")
//                            } else if (height != -1 && width != -1 && height < width) {
////                                Text("LANDSCAPE")
//                            } else {
////                                Text("SQUARE")
//                            }
//                            val imageRequest = ImageRequest.Builder(LocalContext.current)
//                                // TODO fix error image
//                                .error(R.drawable.image_place_holder)
//                                .placeholder(R.drawable.image_place_holder)
//                                .data(imageUrl)
//                                .build()
//                            // 1. link - portrait (show at side), landscape (show full)
//                            // 2. selftext - portrait (show at side), landscape (show full)
//
//                            Image(
//                                painter = rememberImagePainter(imageRequest, imageLoader),
//                                contentDescription = null,
//                                contentScale = ContentScale.FillWidth,
//                                modifier = Modifier
//                                    .fillParentMaxWidth()
//                                    .height(200.dp)
//                                    .padding(top = 8.dp)
//                            )
//                        }
//
//                        /* Content Section */
//                        if (submission.domain.contains("self.androiddev")) {
//                            if (!submission.selfTextHtml.isNullOrBlank()) {
//                                Box(modifier = Modifier.padding(top = 8.dp)) {
//                                    HtmlView(submission.selfTextHtml ?: "(no selfText)")
//                                }
//                            }
//                        } else {
//                            Card(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(vertical = 8.dp),
//                                shape = RoundedCornerShape(4.dp),
//                                backgroundColor = MaterialTheme.colors.Neutral01
//                            ) {
//                                Row(
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    modifier = Modifier
//                                        .height(60.dp)
//                                ) {
//                                    Box(
//                                        modifier = Modifier
//                                            .background(MaterialTheme.colors.Primary08)
//                                            .width(2.dp)
//                                            .fillParentMaxHeight()
//                                    )
//                                    Image(
//                                        modifier = Modifier
//                                            .padding(18.dp)
//                                            .size(20.dp),
//                                        imageVector = ImageVector.vectorResource(
//                                            id = R.drawable.ic_link
//                                        ),
//                                        contentDescription = "Link",
//                                        colorFilter = ColorFilter.tint(MaterialTheme.colors.Neutral10)
//                                    )
////                                Icon(Icons.Outlined.Info, contentDescription = "Link")
//                                    Column(
//                                        modifier = Modifier
//                                    ) {
//                                        Text(
//                                            style = MaterialTheme.typography.body2,
//                                            text = submission.domain,
//                                            color = MaterialTheme.colors.Neutral10
//                                        )
//                                        Text(
//                                            style = MaterialTheme.typography.caption,
//                                            maxLines = 1,
//                                            overflow = TextOverflow.Ellipsis,
//                                            text = submission.url,
//                                            color = MaterialTheme.colors.Neutral10
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                        Text(
//                            style = MaterialTheme.typography.caption,
//                            text = submission.score.toString() + " points " + upvoteRatio
//                        )
//                        Text(
//                            style = MaterialTheme.typography.caption,
//                            text = submission.numComments.toString() + " comments"
//                        )
//                    }
                    RedditContentCard(submission = submission) {
                        context.startActivity(
                            Intent(context, RedditDetailActivity::class.java).apply {
                                putExtra(SubmissionKey, submission)
                            })
                    }
                    Divider(
                        color = Color.LightGray,
                        thickness = 1.dp
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