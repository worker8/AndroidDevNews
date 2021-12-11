package com.worker8.androiddevnews.podcast

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import coil.imageLoader
import com.google.android.exoplayer2.SimpleExoPlayer
import com.icosillion.podengine.models.Episode
import com.icosillion.podengine.models.Podcast
import com.worker8.androiddevnews.ui.HtmlView
import com.worker8.androiddevnews.ui.theme.*
import com.worker8.androiddevnews.util.createImageRequest
import com.worker8.androiddevnews.util.toRelativeTimeString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@ExperimentalTime
@Composable
fun PodcastScreen(
    navController: NavHostController,
    controller: PodcastController,
    viewState: PodcastContract.ViewState,
    input: PodcastContract.Input,
    exoPlayer: SimpleExoPlayer
) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        controller.setInput(
            scope = this,
            input = input,
            viewState = viewState,
            exoPlayer = exoPlayer
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (viewState.podcast.value != null) {
                PodcastList(
                    viewState.podcast,
                    viewState.currentPlayingEpisode,
                    viewState.isPlaying,
                    viewState.lazyListState,
                    input,
                    scope
                )
            } else {
                CircularProgressIndicator()
            }
        }

        if (viewState.currentPlaying.value != null) {
            PlayerControl(viewState, input, scope)
        }
    }
}

@Composable
fun PlayerControl(
    viewState: PodcastContract.ViewState,
    input: PodcastContract.Input,
    scope: CoroutineScope
) {
    Divider(
        color = MaterialTheme.colors.Neutral02,
        thickness = 1.dp
    )
    Row(
        modifier = Modifier
            .height(60.dp)
            .background(MaterialTheme.colors.Neutral01)
            .clickable {
                scope.launch {
                    input.controlPlayClick.emit(Unit)
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        viewState.podcast.value?.imageURL?.let { _url ->
            Image(
                painter = rememberImagePainter(
                    createImageRequest(_url.toString()),
                    LocalContext.current.imageLoader
                ),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .width(40.dp)
                    .height(40.dp)
            )
        }
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp, horizontal = 16.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            text = viewState.currentPlaying.value?.title ?: "Nothing is playing..."
        )
        Icon(
            modifier = Modifier
                .padding(8.dp)
                .width(24.dp),
            imageVector = if (viewState.isPlaying.value) {
                Icons.Outlined.Close
            } else {
                Icons.Outlined.PlayArrow
            },
            contentDescription = null
        )
    }
    LinearProgressIndicator(
        progress = viewState.progress.value,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PodcastList(
    podcastState: State<Podcast?>,
    currentPlayingEpisode: State<Episode?>,
    isPlaying: State<Boolean>,
    lazyListState: LazyListState,
    input: PodcastContract.Input,
    coroutineScope: CoroutineScope
) {
    val podcast = podcastState.value!!
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
                            Text(
                                text = podcast.title ?: "",
                                style = MaterialTheme.typography.subtitle1.copy(color = MaterialTheme.colors.Neutral09),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
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
                    PlayButton(
                        duration = episode.iTunesInfo.duration,
                        isPlaying = currentPlayingEpisode.value?.guid == episode.guid && isPlaying.value
                    ) {
                        Log.d("ddw", "${episode.title}: ${episode.enclosure.url}")
                        coroutineScope.launch {
//                            Log.d("ddw", "[${System.identityHashCode(input.playSharedFlow)}] EMIT!")
                            //episode.enclosure.url.toString()
                            input.listPlayClick.emit(episode)
                        }
                    }
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
fun PlayButton(duration: String, isPlaying: Boolean, callback: () -> Unit) {
    val buttonColor = MaterialTheme.colors.Primary07
    val roundedCornerShape = RoundedCornerShape(50)
    Card(
        modifier = Modifier
            .padding(top = 12.dp)
            .clip(roundedCornerShape)
            .clickable { callback() },
        border = BorderStroke(1.dp, buttonColor),
        shape = roundedCornerShape,
        backgroundColor = MaterialTheme.colors.background
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 12.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
        ) {

            Icon(
                imageVector = if (isPlaying) {
                    Icons.Outlined.Close
                } else {
                    Icons.Outlined.PlayArrow
                },
                contentDescription = "toggle-play-or-pause",
                tint = buttonColor
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
