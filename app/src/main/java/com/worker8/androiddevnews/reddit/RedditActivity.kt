package com.worker8.androiddevnews.reddit

import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import coil.ImageLoader
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.util.DebugLogger
import com.kirkbushman.araw.helpers.AuthUserlessHelper
import com.kirkbushman.araw.models.Submission
import com.kirkbushman.araw.utils.createdDate
import com.worker8.androiddevnews.R
import com.worker8.androiddevnews.di.IoDispatcher
import com.worker8.androiddevnews.ui.theme.AndroidDevNewsTheme
import com.worker8.androiddevnews.util.toRelativeTimeString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.cancel
import javax.inject.Inject

@AndroidEntryPoint
class RedditActivity : AppCompatActivity() {
    @Inject
    lateinit var userlessAuth: AuthUserlessHelper

    @Inject
    @IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    lateinit var controller: RedditController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AndroidDevNewsTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
//                    RedditScreen(controller)
                }
            }
        }
    }
}

@Composable
fun RedditScreen(controller: RedditController, state: MutableState<List<Submission>>) {
    val scope = rememberCoroutineScope()
//    val state: MutableState<List<Submission>> = rememberSaveable { mutableStateOf(listOf<Submission>()) }
    DisposableEffect(scope) {
        controller.setInput(scope, state)
        onDispose {
            scope.cancel()
        }
    }
    RedditList(state)
}

@Composable
fun RedditList(state: State<List<Submission>>) {
    val imageLoader =
        ImageLoader.Builder(LocalContext.current)
            .logger(DebugLogger())
            .build()
    CompositionLocalProvider(LocalImageLoader provides ImageLoader(LocalContext.current)) {
        LazyColumn(
            modifier = Modifier
                .semantics { testTag = "AAABBBB" }
                .background(MaterialTheme.colors.background)
                .fillMaxHeight()
        ) {
            items(
                count = state.value.count(),
                itemContent = { index ->
                    val submission = state.value[index]
                    val upvoteRatio = if (submission.upvoteRatio != null) {
                        "(${submission.upvoteRatio!! * 100}%)"
                    } else {
                        ""
                    }
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = index.toString() + submission.title,
                            style = MaterialTheme.typography.h6
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                style = MaterialTheme.typography.subtitle2,
                                text = submission.author
                            )
                            Text(
                                style = MaterialTheme.typography.caption,
                                text = submission.createdDate.toRelativeTimeString(),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                            submission.linkFlairText?.let { RenderFlair(it) }
                        }
                        val imageUrl = if (submission.url.isImageUrl()) {
                            submission.url
                        } else {
                            submission.preview?.source()?.url?.let {
                                HtmlCompat.fromHtml(it, FROM_HTML_MODE_COMPACT).toString()
                            } ?: ""
                        }
                        Log.d("xmm", "$index: $imageUrl")
                        if (imageUrl.isNotBlank()) {
                            val height = submission.preview?.source()?.height ?: -1
                            val width = submission.preview?.source()?.width ?: -1
                            if (height != -1 && width != -1 && height > width) {
                                Text("PORTRAIT")
                            } else if (height != -1 && width != -1 && height < width) {
                                Text("LANDSCAPE")
                            } else {
                                Text("SQUARE")
                            }
                            val imageRequest = ImageRequest.Builder(LocalContext.current)
                                // TODO fix error image
                                .error(R.drawable.ic_launcher_foreground)
                                // TODO fix placeholder image
                                .placeholder(R.drawable.ic_launcher_background)
                                .data(imageUrl)
                                .build()
                            // 1. link - portrait (show at side), landscape (show full)
                            // 2. selftext - portrait (show at side), landscape (show full)


                            Image(
                                painter = rememberImagePainter(imageRequest, imageLoader),
                                contentDescription = null,
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                    .fillParentMaxWidth()
//                                .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }

                        /* Content Section */
                        if (submission.domain.contains("self.androiddev")) {
                            if (!submission.selfTextHtml.isNullOrBlank()) {
                                HtmlViewEncode(submission.selfTextHtml ?: "(no selfText)")
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Icon(Icons.Outlined.Info, contentDescription = "Link")
                                Column(modifier = Modifier.padding(start = 4.dp)) {
                                    Text(
                                        modifier = Modifier.padding(top = 4.dp),
                                        text = submission.domain
                                    )
                                    Text(
                                        style = MaterialTheme.typography.subtitle2,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        text = submission.url
                                    )
                                }
                            }
                        }
                        Text(
                            style = MaterialTheme.typography.caption,
                            text = submission.score.toString() + " points " + upvoteRatio
                        )
                        Text(
                            style = MaterialTheme.typography.caption,
                            text = submission.numComments.toString() + " comments"
                        )
                    }
                    Divider(
                        modifier = Modifier.padding(16.dp),
                        color = Color.Gray,
                        thickness = 1.dp
                    )
                }
            )
        }
    }
}

@Composable
private fun RenderFlair(text: String) {
    Card(
        Modifier.padding(start = 4.dp),
        shape = RoundedCornerShape(50),
        backgroundColor = Color.Gray,
    ) {
        Text(
            style = MaterialTheme.typography.caption.copy(
                if (isSystemInDarkTheme()) {
                    Color.Black
                } else {
                    Color.White
                }
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
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

// TODO
// 1. refactor controller, then VM
// 2. add hilt
// 3. handle link-only, link-image, self-image
// 4. handle navigation to internal webview
