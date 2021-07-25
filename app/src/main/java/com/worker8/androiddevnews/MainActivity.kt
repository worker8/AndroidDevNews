package com.worker8.androiddevnews

import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
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
import com.worker8.androiddevnews.ui.theme.AndroidDevNewsTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var userlessAuth: AuthUserlessHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val flowState = MutableStateFlow<List<Submission>>(listOf())
        val uiScope = CoroutineScope(Dispatchers.IO)
        uiScope.launch {
            val redditClient = userlessAuth.getRedditClient()
            val submissions =
                redditClient!!.contributionsClient.submissions("androiddev", limit = 40L)
                    .fetchNext()
            val debugStringBuilder = StringBuilder()
            submissions!!.map { it.copy(selfText = "(hidden)", selfTextHtml = "(hidden)") }
                .forEachIndexed { index, submission ->
                    val fields = submission.toString().split(",")
                    fields.forEach {
                        debugStringBuilder.appendLine("#$index:  $it")
                    }
                    debugStringBuilder.appendLine("-------")

                    Log.d(
                        "ddw",
                        "#$index] domain: ${submission.domain}, ${submission.media}"
                    )
//                Log.d("ddw", "--> comment count: ${submission.numComments}")
//                Log.d("ddw", "--> vote count: ${submission.score}")
//                Log.d("ddw", "--> link: ${submission.url}")
//                Log.d("ddw", "--> thumbnail: ${submission.thumbnailUrl}")
//                Log.d("ddw", "--> selfText: ${submission.selfText}")
//                Log.d("ddw", "--> author: ${submission.author}")
                }
            Log.d("ddw", debugStringBuilder.toString())
            flowState.emit(submissions)
        }

        setContent {
            AndroidDevNewsTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    RedditList(flowState)
                }
            }
        }
    }
}

class Contract {
    interface ViewState {
        val state: MutableStateFlow<List<Submission>>
    }
}

class Controller() : Contract.ViewState {
    override val state = MutableStateFlow<List<Submission>>(listOf())
    fun setInput() {

    }
}

@Composable
fun RedditList(flowState: StateFlow<List<Submission>>) {
    val state = flowState.collectAsState()
    val imageLoader =
        ImageLoader.Builder(LocalContext.current)
            .logger(DebugLogger())
            .build()
// Set
    CompositionLocalProvider(LocalImageLoader provides ImageLoader(LocalContext.current)) {
        // Describe the rest of the UI.

        LazyColumn(
            modifier = Modifier
                .semantics { testTag = "AAABBBB" }
                .background(MaterialTheme.colors.background)
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
                            submission.linkFlairText?.let { renderFlair(it) }
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
                                HtmlView(submission.selfTextHtml ?: "(no selfText)")
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
private fun renderFlair(text: String) {
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
