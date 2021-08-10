package com.worker8.androiddevnews.podcast

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import coil.imageLoader
import com.prof.rssparser.Channel
import com.worker8.androiddevnews.ui.HtmlView
import com.worker8.androiddevnews.util.createImageRequest
import kotlinx.coroutines.cancel

@Composable
fun PodcastScreen(
    navController: NavHostController,
    controller: PodcastController,
    state: MutableState<Channel>,
    lazyListState: LazyListState
) {
    val scope = rememberCoroutineScope()
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
    PodcastList(state, lazyListState)
}

@Composable
fun PodcastList(state: State<Channel>, lazyListState: LazyListState) {
    LazyColumn(
        modifier = Modifier.background(MaterialTheme.colors.background),
        state = lazyListState
    ) {
        items(
            count = state.value.articles.size,
            itemContent = { index ->
//                if (index == 0) {
//                    // header
//                    Column {
//                        state.value.image?.url?.let { _url ->
//                            ImageAspectRatio(imageUrl = _url)
//                        }
//                        Text(
//                            modifier = Modifier.padding(16.dp),
//                            text = state.value.title ?: "no title"
//                        )
//                        Text(
//                            modifier = Modifier.padding(16.dp),
//                            text = state.value.description ?: "no description"
//                        )
//                        Text(
//                            modifier = Modifier.padding(16.dp),
//                            text = state.value.link ?: "no link"
//                        )
//                        Text(
//                            modifier = Modifier.padding(16.dp),
//                            text = state.value.lastBuildDate ?: "no lastBuildDate"
//                        )
//                        Text(
//                            modifier = Modifier.padding(16.dp),
//                            text = state.value.updatePeriod ?: "no updatePeriod"
//                        )
//                    }
//                } else {
                val article = state.value.articles[index]
                Column(modifier = Modifier.padding(16.dp)) {
                    Row() {
                        state.value.image?.url?.let { _url ->
                            Image(
                                painter = rememberImagePainter(
                                    createImageRequest(_url),
                                    LocalContext.current.imageLoader
                                ),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(50.dp)
                            )
                        }
                        Column() {
                            Text("Fragmented Podcast (hardcode)")
                            Text("1 hour ago")
                        }
                    }
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
//                }

            })
    }
}