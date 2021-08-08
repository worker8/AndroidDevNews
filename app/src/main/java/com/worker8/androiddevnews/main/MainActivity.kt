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
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.kirkbushman.araw.models.Submission
import com.kirkbushman.araw.models.base.CommentData
import com.prof.rssparser.Channel
import com.worker8.androiddevnews.podcast.PodcastController
import com.worker8.androiddevnews.podcast.PodcastScreen
import com.worker8.androiddevnews.reddit.RedditController
import com.worker8.androiddevnews.reddit.RedditScreen
import com.worker8.androiddevnews.reddit.detail.RedditDetailController
import com.worker8.androiddevnews.reddit.detail.RedditDetailScreen
import com.worker8.androiddevnews.ui.theme.AndroidDevNewsTheme
import com.worker8.androiddevnews.ui.theme.BottomNavBg
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var redditController: RedditController

    @Inject
    lateinit var redditDetailController: RedditDetailController

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
                        redditDetailController = redditDetailController,
                        podcastController = podcastController
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    redditController: RedditController,
    redditDetailController: RedditDetailController,
    podcastController: PodcastController
) {
    val homeScreenState = remember { mutableStateOf(BottomNavRoute.REDDIT) }
    val redditState = remember { mutableStateOf(listOf<Submission>()) }
    val redditListState = rememberLazyListState()

    val redditDetailState = remember { mutableStateOf(listOf<CommentData>()) }

    val podcastState = remember { mutableStateOf(Channel()) }
    val podcastListState = rememberLazyListState()

    val navController = rememberNavController()
    Column {
        NavHost(
            navController = navController,
            startDestination = "reddit_group",
            modifier = Modifier.weight(1f)
        ) {
            composable("reddit_group") {
                val navControllerReddit = rememberNavController()
                NavHost(
                    navController = navControllerReddit,
                    startDestination = BottomNavRoute.REDDIT.toString(),
                    modifier = Modifier.weight(1f)
                ) {
                    composable(BottomNavRoute.REDDIT.toString()) {
                        RedditScreen(
                            navControllerReddit,
                            redditController,
                            redditState,
                            redditListState
                        )
                    }
                    composable(
                        "reddit_detail/{submissionId}",
                        arguments = listOf(navArgument("submissionId") {
                            type = NavType.StringType
                        })
                    ) { backStackEntry ->
                        RedditDetailScreen(
                            redditDetailController,
                            redditDetailState,
                            backStackEntry.arguments?.getString("submissionId")!!
                        )
//                        Text(
//                            text = "Submission Id: ${backStackEntry.arguments?.getString("submissionId")}",
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .padding(16.dp)
//                        )
                    }
                }
            }
            composable(BottomNavRoute.PODCAST.toString()) {
                PodcastScreen(navController, podcastController, podcastState, podcastListState)
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
                navController.navigate("reddit_group")
            },
            label = { Text(text = "Reddit") },
        )
        BottomNavigationItem(
            modifier = Modifier.background(MaterialTheme.colors.BottomNavBg),
            icon = { Icon(imageVector = Icons.Outlined.Phone, contentDescription = null) },
            selected = homeScreenState.value == BottomNavRoute.PODCAST,
            onClick = {
                homeScreenState.value = BottomNavRoute.PODCAST
                navController.navigate(BottomNavRoute.PODCAST.toString())
//                animate = false
            },
            label = { Text(text = "Podcast") },
        )
    }

}