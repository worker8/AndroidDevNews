package com.worker8.androiddevnews

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kirkbushman.araw.helpers.AuthUserlessHelper
import com.worker8.androiddevnews.ui.theme.AndroidDevNewsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
        // reddit app id = AkBN4qqK2Fod7A

        setContent {
            AndroidDevNewsTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
        val uiScope = CoroutineScope(Dispatchers.IO)
        val creds = TestCredentials(
            clientId = BuildConfig.RedditClientId,
            redirectUrl = "",
            scriptClientId = "",
            scriptClientSecret = "",
            username = "",
            password = "",
            scopes = listOf("read", "save", "account")
        )

        val userlessAuth = AuthUserlessHelper(
            context = this,
            clientId = creds.clientId,
            deviceId = UUID.randomUUID().toString(),
            logging = true
        )
        uiScope.launch {
            val redditClient = userlessAuth.getRedditClient()
            Log.d("ddw", "redditClient: $redditClient")
            val submissions =
                redditClient!!.contributionsClient.submissions("androiddev", limit = 100L)
                    .fetchNext()
            submissions!!.forEachIndexed { index, submission ->
                Log.d("ddw", "#[$index]: ${submission.title}")
            }
        }
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