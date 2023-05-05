package com.sample.player.video

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import dagger.hilt.android.lifecycle.HiltViewModel
import formatMinSec
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@UnstableApi
@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    val player: Player
) : ViewModel() {

    private val videoUrl = "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd"
    var lifecycleEvent by mutableStateOf(Lifecycle.Event.ON_CREATE)
    var isVideoPlaying by mutableStateOf(false)
    var videoTimer by mutableStateOf(0L)
    var totalDuration by mutableStateOf(0L)
    var bufferedPercentage by mutableStateOf(0)

    val listener = object : Player.Listener {

        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            isVideoPlaying = player.isPlaying
            totalDuration = player.duration
            bufferedPercentage = player.bufferedPercentage
            if (timerJob?.isActive == false) {
                videoTimer = player.contentPosition
            }
            Log.v("timer", player.contentPosition.formatMinSec())
//            playbackState = player.playbackState
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            runTimer(isPlaying)
        }
    }

    private var timerJob: Job? = null
    fun runTimer(isPlaying: Boolean) {
        timerJob?.cancel()
        if (isPlaying) {
            timerJob = viewModelScope.launch {
                while (true) {
                    delay(1000)
                    videoTimer = player.contentPosition
                }
            }
        }
    }

    fun initListener() {
        player.addListener(listener)
    }

    fun releasePlayer() {
        player.removeListener(listener)
        player.release()
    }

    private fun initializeVideoPlayer(url: String = videoUrl) {
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.playWhenReady = true
        player.addListener(listener)
        player.prepare()
    }

    fun updatePlayerState(playerView: PlayerView) {
        playerView.useController = false
        when (lifecycleEvent) {
            Lifecycle.Event.ON_CREATE -> {
                initializeVideoPlayer()
            }
            Lifecycle.Event.ON_PAUSE -> {
                playerView.onPause()
                playerView.player?.playWhenReady = false
            }
            Lifecycle.Event.ON_RESUME -> {
                playerView.onResume()
                playerView.player?.playWhenReady = true
            }
            else -> Unit
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.removeListener(listener)
        player.release()
    }
}