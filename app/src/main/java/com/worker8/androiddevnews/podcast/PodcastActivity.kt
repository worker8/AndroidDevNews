package com.worker8.androiddevnews.podcast

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.prof.rssparser.Channel
import com.worker8.androiddevnews.ImageAspectRatio
import com.worker8.androiddevnews.reddit.HtmlView
import com.worker8.androiddevnews.ui.theme.AndroidDevNewsTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import javax.inject.Inject


@AndroidEntryPoint
class PodcastActivity : AppCompatActivity() {
    @Inject
    lateinit var podcastController: PodcastController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AndroidDevNewsTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
//                    PodcastScreen(controller = podcastController)
                }
            }
        }
    }
}

@Composable
fun PodcastScreen(controller: PodcastController, state: MutableState<Channel>) {
    val scope = rememberCoroutineScope()
//    val state: MutableState<Channel> = rememberSaveable { mutableStateOf(Channel()) }
    Log.d("ccw", "fromSavaeble: ${System.identityHashCode(state)}")
    DisposableEffect(scope) {
        controller.setInput(scope, state)
        onDispose {
            scope.cancel()
        }
    }

    //controller.state.onEach {
    //    state.value = it
    //}.launchIn(scope)
    PodcastList(state)
}

@Composable
fun PodcastList(state: State<Channel>) {
    LazyColumn(
        modifier = Modifier.background(MaterialTheme.colors.background)
    ) {
        items(
            count = state.value.articles.size + 1,
            itemContent = { index ->
                if (index == 0) {
                    // header
                    Column {
                        state.value.image?.url?.let { _url ->
                            ImageAspectRatio(imageUrl = _url)
                        }
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = state.value.title ?: "no title"
                        )
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = state.value.description ?: "no description"
                        )
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = state.value.link ?: "no link"
                        )
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = state.value.lastBuildDate ?: "no lastBuildDate"
                        )
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = state.value.updatePeriod ?: "no updatePeriod"
                        )
                    }
                } else {
                    val article = state.value.articles[index - 1]
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = article.guid ?: "no guid"
                        )
                        Text(
                            text = article.title ?: "no title"
                        )
                        Text(
                            text = article.author ?: "no author"
                        )
                        Text(
                            text = article.link ?: "no link"
                        )
                        Text(
                            text = article.pubDate ?: "no pubDate"
                        )
                        Text("Content:\n")
                        HtmlView(article.content ?: "no content")

                        Text(
                            text = article.image ?: "no image"
                        )
                        Text(
                            text = article.audio ?: "no audio"
                        )
                        Text(
                            text = article.video ?: "no video"
                        )
                        Text(
                            text = article.sourceName ?: "no sourceName"
                        )
                        Text(
                            text = article.sourceUrl ?: "no sourceUrl"
                        )
                    }
                }

            })
    }
}
