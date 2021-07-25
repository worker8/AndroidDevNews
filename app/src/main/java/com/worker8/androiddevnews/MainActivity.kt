package com.worker8.androiddevnews

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kirkbushman.araw.helpers.AuthUserlessHelper
import com.kirkbushman.araw.models.Submission
import com.kirkbushman.araw.utils.createdDate
import com.worker8.androiddevnews.ui.theme.AndroidDevNewsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val flowState = MutableStateFlow<List<Submission>>(listOf())
        val uiScope = CoroutineScope(Dispatchers.IO)
        uiScope.launch {
            val userlessAuth = AuthUserlessHelper(
                context = this@MainActivity,
                clientId = BuildConfig.RedditClientId,
                deviceId = null,
                logging = true
            )
            val redditClient = userlessAuth.getRedditClient()
            val submissions =
                redditClient!!.contributionsClient.submissions("androiddev", limit = 20L)
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

@Composable
fun RedditList(flowState: StateFlow<List<Submission>>) {
    val state = flowState.collectAsState()

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


                    if (submission.selfText.isNullOrBlank()) {

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
                Divider(modifier = Modifier.padding(16.dp), color = Color.Gray, thickness = 1.dp)
            }
        )
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
