package com.worker8.androiddevnews.podcast

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.worker8.androiddevnews.R
import com.worker8.androiddevnews.main.MainActivity
import com.worker8.androiddevnews.util.tickerFlow
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime


@ExperimentalTime
class PodcastService : Service() {
    private val channelName = "Podcast"
    private val channelID = "PODCAST_CHANNEL_ID"
    private val bindedFlow = MutableSharedFlow<Unit>()

    // Binder given to clients
    private val _binder = LocalBinder()
    private val binder: LocalBinder
        get() {
            Log.d("ccw", "---access binder----")
            return _binder
        }
    private val scope = CoroutineScope(Job())
    private val onStartCommandFlow = MutableSharedFlow<Unit>()
    private var initAction: PodcastServiceAction.Init? = null
    private val rewindAction by lazy {
        PodcastServiceAction.Rewind()
    }
    private val forwardAction by lazy {
        PodcastServiceAction.Forward()
    }
    private val playPauseAction by lazy {
        PodcastServiceAction.PlayPause()
    }
    private val closeAction by lazy {
        PodcastServiceAction.Close()
    }
    private val mediaSession: MediaSessionCompat by lazy {
        MediaSessionCompat(applicationContext, "PodcastService").apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onSeekTo(pos: Long) {
                    exoPlayer.seekTo(pos)
                }
            })
        }
    }
    val progressFlow = MutableSharedFlow<CurrentProgress>()
    val isPlayingFlow = MutableSharedFlow<Boolean>()
    //    var notificationTitle: String? = null
    //    var notificationDesc: String? = null

    companion object {
        const val NotificationId = 0x44323
        const val Parcel = "Parcel"
    }

    val exoPlayer: SimpleExoPlayer by lazy {
        SimpleExoPlayer.Builder(applicationContext).build().apply {
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    scope.launch {
                        isPlayingFlow.emit(isPlaying)
                    }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
//                    Log.d("ddw", "playbackState: $playbackState")
//                    if (playWhenReady && playbackState == Player.STATE_READY) {
//                        Log.d("ccw", "STATE = playing")
//                        // media actually playing
//                    } else if (playWhenReady) {
//                        Log.d("ccw", "STATE = buffering?")
//                        // might be idle (plays after prepare()),
//                        // buffering (plays when data available)
//                        // or ended (plays when seek away from end)
//                    } else {
//                        Log.d("ccw", "STATE = pause")
//                        // player paused in any state
//                    }
//                    when (playbackState) {
//                        STATE_FAST_FORWARDING -> {
//                            Log.d("ddw", "STATE_FAST_FORWARDING")
//                        }
//                        STATE_PAUSED -> {
//                            Log.d("ddw", "STATE_PAUSED")
//                        }
//                        STATE_PLAYING -> {
//                            Log.d("ddw", "STATE_PLAYING")
//                        }
//                    }
                    Intent(applicationContext, PodcastService::class.java).also {
                        startService(it)
                    }
                }
            })
        }
    }


    class CurrentProgress(val title: String, val progress: Float, val iconUrl: String)

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(channelID, channelName)
        }
        bindedFlow
            .onEach {
                Log.d("ccw", "bindedFlow!")
                val progress = exoPlayer.currentPosition.toFloat() / exoPlayer.duration.toFloat()
                progressFlow.emit(
                    CurrentProgress(
                        title = initAction?.title ?: "",
                        progress = progress,
                        iconUrl = initAction?.iconUrl ?: ""
                    )
                )
            }
            .flowOn(Dispatchers.Main)
            .launchIn(scope)
        tickerFlow(Duration.seconds(1))
            .filter { exoPlayer.isPlaying }
            .flowOn(Dispatchers.Main)
            .onEach {
                val progress = exoPlayer.currentPosition.toFloat() / exoPlayer.duration.toFloat()
                progressFlow.emit(
                    CurrentProgress(
                        title = initAction?.title ?: "",
                        progress = progress,
                        iconUrl = initAction?.iconUrl ?: ""
                    )
                )
            }
            .flowOn(Dispatchers.Main)
            .launchIn(scope)
        onStartCommandFlow.onEach {

        }.launchIn(scope)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ddw", "[service] onDestroy")
        scope.cancel()
        exoPlayer.stop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getStringExtra("action")) {
            PodcastServiceAction.Init.name -> {
                val initActionParcel = intent.getParcelableExtra<PodcastServiceAction.Init>(Parcel)
                initAction = PodcastServiceAction.Init(
                    initActionParcel?.title ?: "untitled",
                    initActionParcel?.description ?: "",
                    initActionParcel?.mp3Url ?: "",
                    initActionParcel?.iconUrl ?: ""
                )
                initAction?.onClick(exoPlayer, this@PodcastService)
            }
            PodcastServiceAction.Forward.name -> {
                forwardAction.onClick(exoPlayer, this@PodcastService)
            }
            PodcastServiceAction.Rewind.name -> {
                rewindAction.onClick(exoPlayer, this@PodcastService)
            }
            PodcastServiceAction.PlayPause.name -> {
                playPauseAction.onClick(exoPlayer, this@PodcastService)
            }
            PodcastServiceAction.Close.name -> {
                closeAction.onClick(exoPlayer, this@PodcastService)
            }
        }
        val pendingIntent =
            //TODO - deeplink into podcast
            Intent(applicationContext, MainActivity::class.java).let { notificationIntent ->
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
                .addAction(rewindAction.buildNotificationAction(this, exoPlayer.isPlaying))
                .addAction(playPauseAction.buildNotificationAction(this, exoPlayer.isPlaying))
                .addAction(forwardAction.buildNotificationAction(this, exoPlayer.isPlaying))
                .addAction(closeAction.buildNotificationAction(this, exoPlayer.isPlaying))
                .setContentTitle(initAction?.title)
                .setContentText(initAction?.description)
                .setSmallIcon(R.drawable.exo_icon_circular_play)
                .setContentIntent(pendingIntent)
                .setTicker(initAction?.title)
                .setOnlyAlertOnce(true)
                .build()
            startForeground(NotificationId, notification)
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

    override fun onRebind(intent: Intent?) {
        Log.d("ccw", "REBINDed-----")
        super.onRebind(intent)
        scope.launch {
            bindedFlow.emit(Unit)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d("ccw", "onBinded!!")
        scope.launch {
            bindedFlow.emit(Unit)
        }
//        scope.launch(Dispatchers.Main) {
//            val progress = exoPlayer.currentPosition.toFloat() / exoPlayer.duration.toFloat()
//            progressFlow.emit(
//                CurrentProgress(
//                    title = initAction?.title ?: "",
//                    progress = progress,
//                    iconUrl = initAction?.iconUrl ?: ""
//                )
//            )
//        }
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("ccw", "unbinded......")
        return true
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
}
