package com.worker8.androiddevnews.podcast

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.worker8.androiddevnews.R
import com.worker8.androiddevnews.TestServiceActivity
import com.worker8.androiddevnews.util.tickerFlow
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime


class PodcastService : Service() {
    private val channelName = "Podcast"
    private val channelID = "PODCAST_CHANNEL_ID"

    // Binder given to clients
    private val binder = LocalBinder()
    private val scope = CoroutineScope(Job())

    companion object {
        val notificationId = 0x44323
    }

    private val exoPlayer: SimpleExoPlayer by lazy {
        SimpleExoPlayer.Builder(applicationContext).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    Intent(applicationContext, PodcastService::class.java).also {
                        startService(it)
                    }
                    Log.d("ddw", "play state change: $playbackState")
                }
            })
        }
    }


    /* TODO: delete later */
    private val mp3Url =
        "https://cdn.simplecast.com/audio/20f35050-e836-44cd-8f7f-fd13e8cb2e44/episodes/c52401d1-990c-4a55-8f9f-a6fffa45ed9c/audio/f6de3688-9e3c-4a68-9d00-e7d88087dd21/default_tc.mp3?aid=rss_feed&feed=LpAGSLnY"

    // Random number generator
    private val mGenerator = Random()
    private var _count = 0
    val count: Int
        get() {
            _count++
            return _count
        }

    /** method for clients  */
    val randomNumber: Int
        get() = mGenerator.nextInt(100)

    private val onStartCommandFlow = MutableSharedFlow<Unit>()
    val progressFlow = MutableSharedFlow<Float>()
    private val mediaSession: MediaSessionCompat by lazy {
        MediaSessionCompat(applicationContext, "PodcastService").apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onSeekTo(pos: Long) {
                    exoPlayer.seekTo(pos)
                }
            })
        }
    }

    @ExperimentalTime
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(channelID, channelName)
        }
        tickerFlow(Duration.seconds(1))
            .filter { exoPlayer.isPlaying }
            .flowOn(Dispatchers.Main)
            .onEach {
                val progress = exoPlayer.currentPosition.toFloat() / exoPlayer.duration.toFloat()
                Log.d("ddw", "emit progress: $progress")
                progressFlow.emit(progress)
            }
            .flowOn(Dispatchers.Main)
            .launchIn(scope)
        onStartCommandFlow.onEach {

        }.launchIn(scope)
    }

    var notificationTitle: String? = null
    var notificationDesc: String? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("ddw", "[service] onStartCommand action: ${intent?.action}")

        val initAction = Action.Init(
            notificationTitle ?: "(no title1)",
            notificationDesc ?: "(no description1)",
            intent?.getStringExtra("mp3Url") ?: "",
//            "https://cdn.simplecast.com/audio/20f35050-e836-44cd-8f7f-fd13e8cb2e44/episodes/c52401d1-990c-4a55-8f9f-a6fffa45ed9c/audio/f6de3688-9e3c-4a68-9d00-e7d88087dd21/default_tc.mp3?aid=rss_feed&feed=LpAGSLnY"
        )
        exoPlayer.apply {
            when (intent?.getStringExtra("action")) {
                initAction.name -> {
                    notificationTitle = intent.getStringExtra("title")
                    notificationDesc = intent.getStringExtra("desc")
                    initAction.call(exoPlayer, this@PodcastService)
                }
                Action.Forward.name -> {
                    Action.Forward.call(exoPlayer, this@PodcastService)
                }
                Action.Rewind.name -> {
                    Action.Rewind.call(exoPlayer, this@PodcastService)
                }
                Action.PlayPause.name -> {
                    Action.PlayPause.call(exoPlayer, this@PodcastService)
                }
                Action.Close.name -> {
                    Action.Close.call(exoPlayer, this@PodcastService)
                }
            }
        }

        val pendingIntent =
            Intent(applicationContext, TestServiceActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(applicationContext, 0, notificationIntent, 0)
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val playbackStateCompat = Builder()
                .setActions(ACTION_REWIND or ACTION_PLAY or ACTION_PAUSE or ACTION_FAST_FORWARD or ACTION_SEEK_TO)
                .setState(
                    if (exoPlayer.isPlaying) STATE_PLAYING else STATE_PAUSED,
                    exoPlayer.currentPosition, 1f
                ).build()
            val mediaMetaData =
                MediaMetadataCompat.Builder()
                    .apply {
                        putLong(
                            MediaMetadataCompat.METADATA_KEY_DURATION,
                            exoPlayer.duration
                        )
                    }.build()

            mediaSession.setPlaybackState(playbackStateCompat)
            mediaSession.setMetadata(mediaMetaData)

            val mediaStyle =
                androidx.media.app.NotificationCompat
                    .MediaStyle()
                    .setShowActionsInCompactView(1, 2, 3)
                    .setMediaSession(mediaSession.sessionToken)

            val notification = NotificationCompat.Builder(applicationContext, channelID)
                .setStyle(mediaStyle)
                .addAction(Action.Rewind.buildNotificationAction(this, exoPlayer.isPlaying))
                .addAction(Action.PlayPause.buildNotificationAction(this, exoPlayer.isPlaying))
                .addAction(Action.Forward.buildNotificationAction(this, exoPlayer.isPlaying))
                .addAction(Action.Close.buildNotificationAction(this, exoPlayer.isPlaying))
                .setContentTitle(initAction.title)
                .setContentText(initAction.description)
                .setSmallIcon(R.drawable.exo_icon_circular_play)
                .setContentIntent(pendingIntent)
                .setTicker(initAction.title)
                .setOnlyAlertOnce(true)
                .build()
            startForeground(notificationId, notification)
            mediaSession.isActive = true

        } else {
            TODO("VERSION.SDK_INT < O")
        }

        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): PodcastService = this@PodcastService
    }

    override fun onBind(intent: Intent) = binder

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ddw", "[service] onDestroy")
        scope.cancel()
        exoPlayer.stop()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val channel = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
        return channelId
    }

    sealed interface Action {
        class Init(val title: String, val description: String, val mp3Url: String) : Action {
            override val name = "Init"
            override fun call(exoPlayer: SimpleExoPlayer, service: Service) {
                val mediaItem = MediaItem.fromUri(mp3Url)
                exoPlayer.apply {
                    setMediaItem(mediaItem)
                    prepare()
                    play()
                }
            }

            override fun buildNotificationAction(
                context: Context,
                isPlaying: Boolean
            ): NotificationCompat.Action? = null
        }

        object Forward : Action {
            override val name = "Forward"
            override fun call(exoPlayer: SimpleExoPlayer, service: Service) {
                exoPlayer.seekTo(exoPlayer.currentPosition + 10000)
            }

            override fun buildNotificationAction(
                context: Context,
                isPlaying: Boolean
            ): NotificationCompat.Action? {
                return NotificationCompat.Action.Builder(
                    R.drawable.exo_icon_fastforward,
                    name,
                    context.makeServiceIntent(name)
                ).build()
            }
        }

        object Rewind : Action {
            override val name = "Rewind"
            override fun call(exoPlayer: SimpleExoPlayer, service: Service) {
                exoPlayer.seekTo(exoPlayer.currentPosition - 10000)
            }

            override fun buildNotificationAction(
                context: Context,
                isPlaying: Boolean
            ): NotificationCompat.Action? {
                return NotificationCompat.Action.Builder(
                    R.drawable.exo_icon_rewind,
                    name,
                    context.makeServiceIntent(name)
                ).build()
            }
        }

        object PlayPause : Action {
            override val name = "PlayPause"
            override fun call(exoPlayer: SimpleExoPlayer, service: Service) {
                exoPlayer.playWhenReady = !exoPlayer.isPlaying
            }

            override fun buildNotificationAction(
                context: Context,
                isPlaying: Boolean
            ): NotificationCompat.Action? {
                return NotificationCompat.Action.Builder(
                    if (isPlaying) {
                        R.drawable.exo_icon_pause
                    } else {
                        R.drawable.exo_icon_play
                    },
                    name,
                    context.makeServiceIntent(name)
                ).build()
            }
        }

        object Close : Action {
            override val name = "Close"
            override fun call(
                exoPlayer: SimpleExoPlayer,
                service: Service
            ) {
                Log.d("ddw", "closing actions")
                exoPlayer.stop() // stop exoplayer
//                service.stopSelf() // stop service
//                (service.applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
//                    .cancel(notificationId)
                NotificationManagerCompat.from(service.applicationContext)
                    .cancel(notificationId)
                service.stopForeground(true)
                service.stopSelf()
            }

            override fun buildNotificationAction(
                context: Context,
                isPlaying: Boolean
            ): NotificationCompat.Action? {
                return NotificationCompat.Action.Builder(
                    R.drawable.exo_icon_stop,
                    name,
                    context.makeServiceIntent(name)
                ).build()
            }
        }

        val name: String
        fun call(exoPlayer: SimpleExoPlayer, service: Service)
        fun buildNotificationAction(
            context: Context,
            isPlaying: Boolean
        ): NotificationCompat.Action?

        fun Context.makeServiceIntent(action: String): PendingIntent {
            Log.d("ddw", "makeServiceIntent - $action")
            val intent = Intent(this, PodcastService::class.java)
            intent.putExtra("action", action)
            intent.action = action
            return PendingIntent.getService(
                applicationContext,
                123,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
