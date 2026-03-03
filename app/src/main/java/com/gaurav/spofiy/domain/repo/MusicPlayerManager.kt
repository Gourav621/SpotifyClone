package com.gaurav.spofiy.domain.repo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.annotation.RawRes
import androidx.core.content.ContextCompat
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import com.gaurav.spofiy.SpotifyMediaService
import com.gaurav.spofiy.domain.model.Track
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicPlayerManager @Inject constructor( @ApplicationContext context: Context) {


    val exoPlayer = ExoPlayer.Builder(context).build().apply {
        volume = 1f
        setAudioAttributes(
            androidx.media3.common.AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build(),
            true
        )

        setHandleAudioBecomingNoisy(true)

        repeatMode = Player.REPEAT_MODE_OFF
    }


    @OptIn(UnstableApi::class)
    fun play(context: Context,track: Track) {
startServiceIfNeeded(context = context)
        val mediaItemBuilder = MediaItem.Builder()
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(track.name)
                    .setArtist(track.artistName)
                    .setArtworkUri(Uri.parse(track.imageUrl))
                    .build()
            )

        when {
            track.songResId != 0 -> {
                val rawUri = RawResourceDataSource.buildRawResourceUri(track.songResId)
                mediaItemBuilder.setUri(rawUri)
            }

            !track.previewUrl.isNullOrEmpty() -> {
                mediaItemBuilder.setUri(track.previewUrl)
            }

            else -> return
        }

        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        exoPlayer.setMediaItem(mediaItemBuilder.build())
        exoPlayer.prepare()
        exoPlayer.playWhenReady=true
    }
    init {
        exoPlayer.addListener(object : Player.Listener {

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {

                    Player.STATE_READY -> {
                        // Song ready hai
                    }

                    Player.STATE_BUFFERING -> {
                        // Internet slow hai – show loader
                    }



                    Player.STATE_IDLE -> {
                        // Media load nahi hua
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e("PlayerError", error.message ?: "Unknown Error")
            }
        })
    }

    fun pause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        }
    }

    fun resume() {
        if (!exoPlayer.isPlaying) {
            exoPlayer.play()
        }
    }

    fun isPlaying(): Boolean = exoPlayer.isPlaying

    /**
     * Returns total duration. Always returns 0L instead of negative values.
     */
    @OptIn(UnstableApi::class)
    fun getDuration(): Long {
        return if (exoPlayer.playbackState == Player.STATE_READY) {
            if (exoPlayer.duration != C.TIME_UNSET) {
                exoPlayer.duration.coerceAtLeast(0L)
            } else {
                0L
            }
        } else {
            0L
        }
    }

    /**
     * Returns current position. Always returns 0L instead of negative values.
     */
    fun currentPosition(): Long {
        return exoPlayer.currentPosition
    }

    fun seekTo(position: Long) {
        val duration = getDuration()
        if (duration != null) {
            exoPlayer.seekTo(position.coerceIn(0L, duration))
        } else {
            exoPlayer.seekTo(position)
        }
    }
    fun startServiceIfNeeded(context: Context) {
        val intent = Intent(context, SpotifyMediaService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }
    fun stop() {
        exoPlayer.stop()
    }

    fun release() {
        exoPlayer.release()
    }
}
