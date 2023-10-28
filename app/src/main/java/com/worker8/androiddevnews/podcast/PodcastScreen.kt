package com.worker8.androiddevnews.podcast

import android.content.res.ColorStateList
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import com.google.android.exoplayer2.ui.PlayerView
import com.worker8.androiddevnews.common.compose.HtmlView
import com.worker8.androiddevnews.common.compose.theme.Neutral01
import com.worker8.androiddevnews.common.compose.theme.Neutral02
import com.worker8.androiddevnews.common.compose.theme.Neutral09
import com.worker8.androiddevnews.common.compose.theme.Neutral10
import com.worker8.androiddevnews.common.compose.theme.Primary07
import com.worker8.androiddevnews.common.util.DurationParser
import com.worker8.androiddevnews.common.util.toRelativeTimeString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@ExperimentalCoilApi
@OptIn(ExperimentalMaterialApi::class)
@ExperimentalTime
@Composable
fun PodcastScreen(
    navController: NavHostController,
    controller: PodcastController,
    viewState: PodcastContract.ViewState,
    input: PodcastContract.Input,
) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        controller.setInput(
            scope = this,
            input = input,
            viewState = viewState,
        )
    }
    val bottomSheetScaffoldState =
        rememberBottomSheetScaffoldState(rememberDrawerState(initialValue = DrawerValue.Closed))
    BottomSheetScaffold(
        modifier = Modifier.wrapContentHeight(),
        scaffoldState = bottomSheetScaffoldState,
        drawerScrimColor = Color.Blue,
        sheetBackgroundColor = Color.Green,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            // minHeight = 1.dp is needed to prevent a crash: https://stackoverflow.com/a/68624825/75579
            Column(
                modifier = Modifier
                    .defaultMinSize(minHeight = 1.dp)
                    .height((LocalConfiguration.current.screenHeightDp * 3 / 4).dp)
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colors.background)
            ) {
                viewState.currentPlayingEpisode.value?.podcastImageUrl?.let { _url ->
                    AsyncImage(
                        model = _url,
//                        "https://upload.wikimedia.org/wikipedia/commons/1/12/ThreeTimeAKCGoldWinnerPembrookeWelshCorgi.jpg"
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .padding(32.dp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Text(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colors.onBackground,
                        text = viewState.currentPlaying.value?.title ?: "Nothing is playing..."
                    )
                }
                val exoPlayer by input.exoPlayer.collectAsState(initial = null)
                exoPlayer?.let {
                    AndroidView(
                        factory = { context ->
                            PlayerView(context).apply {
//                            setShutterBackgroundColor(android.graphics.Color.CYAN)
                                setBackgroundColor(android.graphics.Color.BLACK)
                                this.backgroundTintList =
                                    ColorStateList.valueOf(android.graphics.Color.BLACK)
                                player = it
                                showController()
                                val dpAsPixels = (16 * resources.displayMetrics.density + 0.5f)
                                setPadding(0, dpAsPixels.toInt(), 0, dpAsPixels.toInt())
                                controllerShowTimeoutMs = 0
                                controllerHideOnTouch = false
                            }
                        },
                    )
                }
            }
        }) {

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                        if (viewState.episodePairs.value.isNotEmpty()) {
                            PodcastList(
                                viewState.episodePairs,
                                viewState.currentPlayingEpisode,
                                viewState.isPlaying,
                                viewState.isLoading,
                                viewState.lazyListState,
                                input,
                                scope
                            )
                        } else {
                            CircularProgressIndicator()
                        }
                    }

                    if (viewState.currentPlaying.value != null) {
                        PlayerControl(
                            viewState,
                            input,
                            scope,
                            bottomSheetScaffoldState.bottomSheetState
                        )
                    }
                }
            }

            if (bottomSheetScaffoldState.bottomSheetState.isExpanded ||
                bottomSheetScaffoldState.bottomSheetState.isAnimationRunning
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f))
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalTime
@Composable
fun PlayerControl(
    viewState: PodcastContract.ViewState,
    input: PodcastContract.Input,
    scope: CoroutineScope,
    bottomSheetState: BottomSheetState
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
                if (bottomSheetState.isExpanded) {
                    scope.launch {
                        bottomSheetState.collapse()
                    }
                } else {
                    scope.launch {
                        bottomSheetState.expand()
                    }
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        //TODO: fix podcast.value
        viewState.currentPlayingEpisode.value?.podcastImageUrl?.let { _url ->
            AsyncImage(
                model = _url,
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

@ExperimentalTime
@Composable
fun PodcastList(
    episodePairsState: State<List<PodcastContract.EpisodePair>>,
    currentPlayingEpisode: State<PodcastContract.EpisodePair?>,
    isPlaying: State<Boolean>,
    isLoading: State<Boolean>,
    lazyListState: LazyListState,
    input: PodcastContract.Input,
    coroutineScope: CoroutineScope
) {
    val episodePairs = episodePairsState.value
    LazyColumn(
        modifier = Modifier.background(MaterialTheme.colors.background),
        state = lazyListState
    ) {
        items(
            count = episodePairs.size,
            key = { index -> episodePairs[index].episode.guid },
            itemContent = { index ->
                val episode = episodePairs[index].episode
                val podcastImageUrl = episodePairs[index].podcastImageUrl
                val podcastTitle = episodePairs[index].podcastTitle
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = podcastImageUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp)
                        )
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            Text(
                                text = podcastTitle,
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
//                    val exoPlayer by input.exoPlayer.collectAsState(initial = null)
                    PlayButton(
                        duration = episode.iTunesInfo.duration,
                        isPlaying = currentPlayingEpisode.value?.episode?.guid == episode.guid && isPlaying.value,
                        isLoading = currentPlayingEpisode.value?.episode?.guid == episode.guid && isLoading.value
                    ) {
                        Log.d("ddw", "${episode.title}: ${episode.enclosure.url}")
                        coroutineScope.launch {
                            input.listPlayClick.emit(episodePairs[index])
                        }
                    }
                    Divider(
                        color = MaterialTheme.colors.Neutral02,
                        thickness = 1.dp,
                        modifier = Modifier.padding(top = 32.dp)
                    )
                }
            }
        )
    }
}

@Composable
private fun PlayButton(
    duration: String,
    isPlaying: Boolean,
    isLoading: Boolean,
    callback: () -> Unit
) {
    Log.d("ddw", "isLoading: $isLoading")
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
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
//                        .padding(start = 8.dp)
                        .width(20.dp)
                        .height(20.dp),
                    color = MaterialTheme.colors.primary,
                    strokeWidth = 2.dp
                )
            } else if (isPlaying) {
                Text(
                    text = "pause",
                    style = MaterialTheme.typography.caption.copy(
                        color = MaterialTheme.colors.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.PlayArrow,
                    contentDescription = "play button",
                    tint = buttonColor
                )
                Text(
                    text = DurationParser.parse(duration),
                    style = MaterialTheme.typography.caption.copy(
                        color = MaterialTheme.colors.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun ModalBottomSheetSample() {
    val state = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            LazyColumn {
                items(50) {
                    ListItem(
                        text = { Text("Item $it") },
                        icon = {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = "Localized description"
                            )
                        }
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Rest of the UI")
            Spacer(Modifier.height(20.dp))
            Button(onClick = { scope.launch { state.show() } }) {
                Text("Click to show sheet")
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun BottomSheetControls(url: String) {
    Column {
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(start = 8.dp)
                .width(40.dp)
                .height(40.dp)
        )
    }
}