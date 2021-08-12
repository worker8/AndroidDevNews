package com.worker8.androiddevnews.podcast

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import coil.imageLoader
import com.icosillion.podengine.models.Podcast
import com.worker8.androiddevnews.ui.HtmlView
import com.worker8.androiddevnews.ui.theme.*
import com.worker8.androiddevnews.util.createImageRequest
import com.worker8.androiddevnews.util.toRelativeTimeString
import kotlinx.coroutines.cancel

@Composable
fun PodcastScreen(
    navController: NavHostController,
    controller: PodcastController,
    state: MutableState<Podcast?>,
    lazyListState: LazyListState
) {
    val scope = rememberCoroutineScope()
    DisposableEffect(scope) {
        controller.setInput(scope, state)
        onDispose {
            scope.cancel()
        }
    }

    //controller.state.onEach {
    //    state.value = it
    //}.launchIn(scope)
    if (state.value != null) {
        PodcastList(state, lazyListState)
    }
}

@Composable
fun PodcastList(state: State<Podcast?>, lazyListState: LazyListState) {
    val podcast = state.value!!
    LazyColumn(
        modifier = Modifier.background(MaterialTheme.colors.background),
        state = lazyListState
    ) {
        items(
            count = podcast.episodes.size,
            itemContent = { index ->
                val episode = podcast.episodes[index]
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        podcast.imageURL?.let { _url ->
                            Image(
                                painter = rememberImagePainter(
                                    createImageRequest(_url.toString()),
                                    LocalContext.current.imageLoader
                                ),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(40.dp)
                            )
                        }
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
//                            val format =
//                                SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ZZZZZ", Locale.ENGLISH)
                            Text(
                                text = podcast.title ?: "",
                                style = MaterialTheme.typography.subtitle1.copy(color = MaterialTheme.colors.Neutral09),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
//                                text = format.parse(episode.pubDate).toRelativeTimeString(),
                                text = episode.pubDate.toRelativeTimeString(),
                                style = MaterialTheme.typography.caption,
                            )
                        }
                    }
                    Text(
                        modifier = Modifier.padding(top = 10.dp, bottom = 6.dp),
                        text = episode.title ?: "(untitled)",
                        style = MaterialTheme.typography.h6.copy(
                            color = MaterialTheme.colors.Neutral10,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        style = MaterialTheme.typography.subtitle2.copy(
                            color = MaterialTheme.colors.onBackground
                        ),
                        text = episode.iTunesInfo.author
                    )
                    HtmlView(
                        content = episode.description ?: "(no summary)",
                        truncation = true
                    )
                    PlayButton(duration = episode.iTunesInfo.duration)
//                    Button(
//                        onClick = { /*TODO*/ },
//                        modifier = Modifier.background(Color.Transparent)
//                    ) {
//
//                    }
                    Divider(
                        color = MaterialTheme.colors.Neutral02,
                        thickness = 1.dp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

            })
    }
}

@Composable
fun PlayButton(duration: String) {
    Card(
        modifier = Modifier.padding(top = 12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colors.Neutral03),
        shape = RoundedCornerShape(50),
        backgroundColor = Color.Transparent
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 12.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "play-arrow",
                tint = MaterialTheme.colors.Primary06
            )
            val durationList = duration.split(":")
            val hour = if (durationList.first() != "00") {
                durationList.first().toInt().toString() + "h "
            } else {
                ""
            }
            val min = durationList[1] + " mins"
            Text(
                text = "$hour$min",
                style = MaterialTheme.typography.caption.copy(
                    color = MaterialTheme.colors.onBackground,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
