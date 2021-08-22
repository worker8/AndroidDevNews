package com.worker8.androiddevnews.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.exoplayer2.SimpleExoPlayer
import com.icosillion.podengine.models.Episode
import com.icosillion.podengine.models.Podcast
import com.kirkbushman.araw.models.Submission
import com.worker8.androiddevnews.podcast.PodcastContract
import com.worker8.androiddevnews.podcast.PodcastController
import com.worker8.androiddevnews.podcast.PodcastScreen
import com.worker8.androiddevnews.reddit.RedditController
import com.worker8.androiddevnews.reddit.RedditScreen
import com.worker8.androiddevnews.ui.theme.AndroidDevNewsTheme
import com.worker8.androiddevnews.ui.theme.BottomNavBg
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var redditController: RedditController

    @Inject
    lateinit var podcastController: PodcastController

    @Inject
    lateinit var simpleExoPlayer: SimpleExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val input = object : PodcastContract.Input {
            override val listPlayClick = MutableSharedFlow<Episode>()
            override val controlPlayClick = MutableSharedFlow<Unit>()
        }
        setContent {
            AndroidDevNewsTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen(
                        redditController = redditController,
                        podcastController = podcastController,
                        input = input,
                        exoPlayer = simpleExoPlayer
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    redditController: RedditController,
    podcastController: PodcastController,
    input: PodcastContract.Input,
    exoPlayer: SimpleExoPlayer
) {
    val homeScreenState = remember { mutableStateOf(BottomNavRoute.REDDIT) }
    val redditState = remember { mutableStateOf(listOf<Submission>()) }
    val redditListState = rememberLazyListState()

//    val podcastState =
//    val podcastListState =

    val viewState = object : PodcastContract.ViewState {
        override val podcast = remember { mutableStateOf<Podcast?>(null) }
        override val currentPlayingEpisode = remember { mutableStateOf<Episode?>(null) }
        override val isPlaying = remember { mutableStateOf(false) }
        override val lazyListState = rememberLazyListState()
    }
    val navController = rememberNavController()
    Column {
        NavHost(
            navController = navController,
            startDestination = BottomNavRoute.REDDIT.toString(),
            modifier = Modifier.weight(1f)
        ) {
            composable(BottomNavRoute.REDDIT.toString()) {
                RedditScreen(
                    navController,
                    redditController,
                    redditState,
                    redditListState
                )
            }
            composable(BottomNavRoute.PODCAST.toString()) {
                PodcastScreen(
                    navController,
                    podcastController,
                    viewState,
                    input,
                    exoPlayer
                )
            }
        }
        BottomNavigationContent(navController = navController, homeScreenState = homeScreenState)
//        Row(modifier = Modifier.weight(1f)) {
//            if (homeScreenState.value == BottomNavType.HOME) {
//                RedditScreen(redditController, redditState, redditListState)
//            } else {
//                PodcastScreen(podcastController, podcastState, podcastListState)
//            }
//        }
    }
}

enum class BottomNavRoute {
    REDDIT,
    PODCAST
}

@Composable
fun BottomNavigationContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    homeScreenState: MutableState<BottomNavRoute>
) {
    BottomNavigation(modifier = modifier) {
        BottomNavigationItem(
            modifier = Modifier.background(MaterialTheme.colors.BottomNavBg),
            icon = { Icon(imageVector = Icons.Outlined.Home, contentDescription = null) },
            selected = homeScreenState.value == BottomNavRoute.REDDIT,
            onClick = {
                homeScreenState.value = BottomNavRoute.REDDIT
                navController.navigate(BottomNavRoute.REDDIT.toString())
            },
            label = {
                Text(
                    text = "Reddit", style = MaterialTheme.typography.caption.copy(
                        color = Color.White
                    )
                )
            },
        )
        BottomNavigationItem(
            modifier = Modifier.background(MaterialTheme.colors.BottomNavBg),
            icon = { Icon(imageVector = Icons.Outlined.Phone, contentDescription = null) },
            selected = homeScreenState.value == BottomNavRoute.PODCAST,
            onClick = {
                homeScreenState.value = BottomNavRoute.PODCAST
                navController.navigate(BottomNavRoute.PODCAST.toString())
            },
            label = {
                Text(
                    text = "Podcast", style = MaterialTheme.typography.caption.copy(
                        color = Color.White
                    )
                )
            },
        )
    }
}
