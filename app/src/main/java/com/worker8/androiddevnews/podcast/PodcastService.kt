package com.worker8.androiddevnews.podcast

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.Parcelable
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
import com.worker8.androiddevnews.main.MainActivity
import com.worker8.androiddevnews.util.tickerFlow
import kotlinx.android.parcel.Parcelize
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
        const val NotificationId = 0x44323
        const val Parcel = "Parcel"
    }

    private val exoPlayer: SimpleExoPlayer by lazy {
        SimpleExoPlayer.Builder(applicationContext).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    Intent(applicationContext, PodcastService::class.java).also {
                        startService(it)
                    }
                }
            })
        }
    }

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
                progressFlow.emit(progress)
            }
            .flowOn(Dispatchers.Main)
            .launchIn(scope)
        onStartCommandFlow.onEach {

        }.launchIn(scope)
    }

    //    var notificationTitle: String? = null
//    var notificationDesc: String? = null
    private var initAction: Action.Init? = null
    private val rewindAction by lazy {
        Action.Rewind()
    }
    private val forwardAction by lazy {
        Action.Forward()
    }
    private val playPauseAction by lazy {
        Action.PlayPause()
    }
    private val closeAction by lazy {
        Action.Close()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getStringExtra("action")) {
            Action.Init.name -> {
                val initActionParcel = intent.getParcelableExtra<Action.Init>(Parcel)
                initAction = Action.Init(
                    initActionParcel?.title ?: "untitled",
                    initActionParcel?.description ?: "",
                    initActionParcel?.mp3Url ?: "",
                )
                initAction?.onClick(exoPlayer, this@PodcastService)
            }
            Action.Forward.name -> {
                forwardAction.onClick(exoPlayer, this@PodcastService)
            }
            Action.Rewind.name -> {
                rewindAction.onClick(exoPlayer, this@PodcastService)
            }
            Action.PlayPause.name -> {
                playPauseAction.onClick(exoPlayer, this@PodcastService)
            }
            Action.Close.name -> {
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
        @Parcelize
        data class Init(val title: String, val description: String, val mp3Url: String) : Action,
            Parcelable {
            override fun onClick(exoPlayer: SimpleExoPlayer, service: Service) {
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

            companion object {
                const val name = "Init"
            }
        }

        class Forward : Action {
            override fun onClick(exoPlayer: SimpleExoPlayer, service: Service) {
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

            companion object {
                const val name = "Forward"
            }
        }

        class Rewind : Action {
            override fun onClick(exoPlayer: SimpleExoPlayer, service: Service) {
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

            companion object {
                const val name = "Rewind"
            }
        }

        class PlayPause : Action {
            override fun onClick(exoPlayer: SimpleExoPlayer, service: Service) {
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

            companion object {
                const val name = "PlayPause"
            }
        }

        class Close : Action {
            override fun onClick(
                exoPlayer: SimpleExoPlayer,
                service: Service
            ) {
                exoPlayer.stop() // stop exoplayer
                // TODO - is stopForeground & stopSelf both needed?
                NotificationManagerCompat.from(service.applicationContext)
                    .cancel(NotificationId)
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

            companion object {
                const val name = "Close"
            }
        }

        fun onClick(exoPlayer: SimpleExoPlayer, service: Service)
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
