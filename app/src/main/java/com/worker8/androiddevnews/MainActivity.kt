package com.worker8.androiddevnews

import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
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

data class TestCredentials(

    val clientId: String,
    val redirectUrl: String,

    val scriptClientId: String,
    val scriptClientSecret: String,
    val username: String,
    val password: String,

    val scopes: List<String>
)

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
            Log.d("ddw", "redditClient: $redditClient")
            val submissions =
                redditClient!!.contributionsClient.submissions("androiddev", limit = 10L)
                    .fetchNext()

            submissions!!.map { it.copy(selfText = "hidden", selfTextHtml = "hidden") }
                .forEachIndexed { index, submission ->
                    val fields = submission.toString().split(",")
                    fields.forEach {
                        Log.d("ddw", "#[$index]: ${it}")
                    }
//                Log.d("ddw", "--> comment count: ${submission.numComments}")
//                Log.d("ddw", "--> vote count: ${submission.score}")
//                Log.d("ddw", "--> link: ${submission.url}")
//                Log.d("ddw", "--> thumbnail: ${submission.thumbnailUrl}")
//                Log.d("ddw", "--> selfText: ${submission.selfText}")
//                Log.d("ddw", "--> author: ${submission.author}")
                }
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
                        text = submission.title,
                        style = MaterialTheme.typography.h6
                    )
                    Row {
                        Text(
                            text = submission.author
                        )
                        Text(
                            text = submission.createdDate.toRelativeTimeString(),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    if (submission.selfText.isNullOrBlank()) {

                    }
                    Text(text = submission.score.toString() + " points " + upvoteRatio)
                    Text(text = submission.numComments.toString() + " comments")
                }
                Divider(modifier = Modifier.padding(16.dp), color = Color.Gray, thickness = 1.dp)
            }
        )
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndroidDevNewsTheme {
        Greeting("Android")
    }
}


fun Date.toRelativeTimeString() =
    DateUtils
        .getRelativeTimeSpanString(time, Date().time, DateUtils.MINUTE_IN_MILLIS)
        .toString()
        .lowercase()
