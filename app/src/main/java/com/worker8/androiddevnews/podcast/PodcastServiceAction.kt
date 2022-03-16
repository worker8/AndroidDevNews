package com.worker8.androiddevnews.podcast

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.worker8.androiddevnews.R
import kotlinx.android.parcel.Parcelize
import kotlin.time.ExperimentalTime

@ExperimentalTime
sealed interface PodcastServiceAction {
    @Parcelize
    data class Init(
        val title: String,
        val description: String,
        val mp3Url: String,
        val iconUrl: String
    ) : PodcastServiceAction,
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

    class Forward : PodcastServiceAction {
        override fun onClick(exoPlayer: SimpleExoPlayer, service: Service) {
            exoPlayer.seekTo(exoPlayer.currentPosition + 10000)
        }

        override fun buildNotificationAction(
            context: Context,
            isPlaying: Boolean
        ): NotificationCompat.Action {
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

    class Rewind : PodcastServiceAction {
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

    class PlayPause : PodcastServiceAction {
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

    class Close : PodcastServiceAction {
        override fun onClick(
            exoPlayer: SimpleExoPlayer,
            service: Service
        ) {
            exoPlayer.stop() // stop exoplayer
            // TODO - is stopForeground & stopSelf both needed?
            NotificationManagerCompat.from(service.applicationContext)
                .cancel(PodcastService.NotificationId)
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