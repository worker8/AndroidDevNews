package com.worker8.androiddevnews.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.kirkbushman.araw.models.Submission
import com.prof.rssparser.Channel
import com.worker8.androiddevnews.podcast.PodcastController
import com.worker8.androiddevnews.podcast.PodcastScreen
import com.worker8.androiddevnews.reddit.RedditController
import com.worker8.androiddevnews.reddit.RedditScreen
import com.worker8.androiddevnews.ui.theme.AndroidDevNewsTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var redditController: RedditController

    @Inject
    lateinit var podcastController: PodcastController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AndroidDevNewsTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen(
                        redditController = redditController,
                        podcastController = podcastController
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(redditController: RedditController, podcastController: PodcastController) {
    val homeScreenState = rememberSaveable { mutableStateOf(BottomNavType.HOME) }
    val redditState = rememberSaveable { mutableStateOf(listOf<Submission>()) }
    val podcastState = rememberSaveable { mutableStateOf(Channel()) }
    Column {
        Row(modifier = Modifier.weight(1f)) {
            if (homeScreenState.value == BottomNavType.HOME) {
                RedditScreen(redditController, redditState)
            } else {
                PodcastScreen(podcastController, podcastState)
            }
        }
        BottomNavigationContent(homeScreenState = homeScreenState)
    }
}

enum class BottomNavType {
    HOME,
    PODCAST
}

@Composable
fun BottomNavigationContent(
    modifier: Modifier = Modifier,
    homeScreenState: MutableState<BottomNavType>
) {
    BottomNavigation(modifier = modifier) {
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Outlined.Home, contentDescription = null) },
            selected = homeScreenState.value == BottomNavType.HOME,
            onClick = {
                homeScreenState.value = BottomNavType.HOME
//                animate = false
            },
            label = { Text(text = "Reddit") },
        )
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Outlined.Phone, contentDescription = null) },
            selected = homeScreenState.value == BottomNavType.PODCAST,
            onClick = {
                homeScreenState.value = BottomNavType.PODCAST
//                animate = false
            },
            label = { Text(text = "Podcast") },
        )
    }

}