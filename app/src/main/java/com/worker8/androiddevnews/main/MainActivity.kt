package com.worker8.androiddevnews.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.exoplayer2.ExoPlayer
import com.kirkbushman.araw.models.Submission
import com.worker8.androiddevnews.R
import com.worker8.androiddevnews.common.compose.theme.AndroidDevNewsTheme
import com.worker8.androiddevnews.common.compose.theme.BottomNavBg
import com.worker8.androiddevnews.newsletter.NewsletterScreen
import com.worker8.androiddevnews.podcast.PodcastContract
import com.worker8.androiddevnews.podcast.PodcastController
import com.worker8.androiddevnews.podcast.PodcastScreen
import com.worker8.androiddevnews.podcast.PodcastService
import com.worker8.androiddevnews.podcast.PodcastServiceAction
import com.worker8.androiddevnews.reddit.RedditController
import com.worker8.androiddevnews.reddit.RedditScreen
import com.worker8.androiddevnews.ui.WebViewActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


@ExperimentalTime
@AndroidEntryPoint
@OptIn(ExperimentalMaterialApi::class)
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var redditController: RedditController

    @Inject
    lateinit var podcastController: PodcastController

    private var mBound: Boolean = false
    private lateinit var mService: PodcastService
    val progressFlow = MutableSharedFlow<PodcastService.CurrentProgress>(replay = 1)
    val isPlayingFlow = MutableSharedFlow<Boolean>()
    val exoPlayerFlow = MutableSharedFlow<ExoPlayer>(replay = 1)

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {
        val scope = CoroutineScope(Job())
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as PodcastService.LocalBinder
            mService = binder.getService()
            mBound = true
            Log.d("ddw", "onServiceConnected")
            scope.launch {
                exoPlayerFlow.emit(mService.exoPlayer)
            }
            mService.progressFlow
                .onEach {
                    Log.d("ccw", "emit2: $it")
                    progressFlow.emit(it)
                }
                .flowOn(Dispatchers.Main)
                .launchIn(scope)
            mService.isPlayingFlow
                .onEach {
                    isPlayingFlow.emit(it)
                }
                .flowOn(Dispatchers.Main)
                .launchIn(scope)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
            scope.cancel()
        }
    }

    @OptIn(ExperimentalTime::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val input = object : PodcastContract.Input {
            override val listPlayClick = MutableSharedFlow<PodcastContract.EpisodePair>()
            override val controlPlayClick = MutableSharedFlow<Unit>()
            override val isPlaying = isPlayingFlow
            override val update = progressFlow
            override val startServiceCallback: (String, String, String, String) -> Unit =
                { title, desc, mp3Url, iconUrl ->
                    Intent(this@MainActivity, PodcastService::class.java).also { _intent ->
                        val initAction = PodcastServiceAction.Init(
                            title = title,
                            description = desc,
                            mp3Url = mp3Url,
                            iconUrl = iconUrl
                        )
                        _intent.putExtra("action", "Init") // TODO, hardcode
                        _intent.putExtra(PodcastService.Parcel, initAction)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(_intent)
                        } else {
                            startService(_intent)
                        }
//                        bindService(_intent, connection, Context.BIND_NOT_FOREGROUND)
                    }
                }
            override val exoPlayer = exoPlayerFlow
        }
        setContent {
            AndroidDevNewsTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen(
                        redditController = redditController,
                        podcastController = podcastController,
                        input = input,
                    )
                }
            }
        }
        bindService(
            Intent(this@MainActivity, PodcastService::class.java),
            connection,
            Context.BIND_NOT_FOREGROUND
        )
    }

    override fun onStop() {
        super.onStop()
        if (mBound) {
            unbindService(connection)
            mBound = false
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalTime
@Composable
fun MainScreen(
    redditController: RedditController,
    podcastController: PodcastController,
    input: PodcastContract.Input,
) {
    val homeScreenState = remember { mutableStateOf(BottomNavRoute.REDDIT) }
    val redditState = remember { mutableStateOf(listOf<Submission>()) }
    val redditListState = rememberLazyListState()
    val context = LocalContext.current
    val viewState = object : PodcastContract.ViewState {
        override val episodePairs =
            remember { mutableStateOf(listOf<PodcastContract.EpisodePair>()) }
        override val progress = remember { mutableStateOf(0f) }
        override val currentPlaying =
            remember { mutableStateOf<PodcastService.CurrentProgress?>(null) }
        override val currentPlayingEpisode =
            remember { mutableStateOf<PodcastContract.EpisodePair?>(null) }
        override val isPlaying = remember { mutableStateOf(false) }
        override val lazyListState = rememberLazyListState()
        override val bottomSheetControlIsOpen =
            remember { mutableStateOf(ModalBottomSheetValue.Hidden) }
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
                ) { url ->
                    val intent = Intent(context, WebViewActivity::class.java).apply {
                        putExtra(WebViewActivity.UrlKey, url)
                    }
                    context.startActivity(intent)
                }
            }

            composable(BottomNavRoute.PODCAST.toString()) {
                PodcastScreen(
                    navController,
                    podcastController,
                    viewState,
                    input,
                )
            }
            composable(BottomNavRoute.ANDROID_WEEKLY.toString()) {
                NewsletterScreen()
            }
        }
        BottomNavigationContent(navController = navController, homeScreenState = homeScreenState)
    }
//        Row(modifier = Modifier.weight(1f)) {
//            if (homeScreenState.value == BottomNavType.HOME) {
//                RedditScreen(redditController, redditState, redditListState)
//            } else {
//                PodcastScreen(podcastController, podcastState, podcastListState)
//            }
//        }
}


enum class BottomNavRoute {
    REDDIT,
    PODCAST,
    ANDROID_WEEKLY
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
        BottomNavigationItem(
            modifier = Modifier.background(MaterialTheme.colors.BottomNavBg),
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_android),
                    contentDescription = null
                )
            },
            selected = homeScreenState.value == BottomNavRoute.ANDROID_WEEKLY,
            onClick = {
                homeScreenState.value = BottomNavRoute.ANDROID_WEEKLY
                navController.navigate(BottomNavRoute.ANDROID_WEEKLY.toString())
            },
            label = {
                Text(
                    text = "Newsletter", style = MaterialTheme.typography.caption.copy(
                        color = Color.White
                    )
                )
            },
        )
    }
}
