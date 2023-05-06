package com.sample.player.video

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.sample.player.AppConstants
import com.sample.player.analytics.AnalyticsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@UnstableApi
@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    val player: Player,
    private val analyticsService: AnalyticsService
) : ViewModel() {

    private val videoUrl = "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd"
    var isVideoPlaying by mutableStateOf(false)
    var videoTimer by mutableStateOf(0L)
    var totalDuration by mutableStateOf(0L)
    var bufferedPercentage by mutableStateOf(0)
    var pauseCount by mutableStateOf(0)
    var forwardCount by mutableStateOf(0)
    var backWardCount by mutableStateOf(0)


    private val listener = object : Player.Listener {

        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            totalDuration = player.duration
            bufferedPercentage = player.bufferedPercentage
            if (videoTimerJob?.isActive == false) {
                videoTimer = player.contentPosition
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            isVideoPlaying = isPlaying
            runVideoTimer(isPlaying)
        }
    }

    private var videoTimerJob: Job? = null
    fun runVideoTimer(isPlaying: Boolean) {
        videoTimerJob?.cancel()
        if (isPlaying) {
            videoTimerJob = viewModelScope.launch {
                while (true) {
                    delay(1000)
                    videoTimer = player.contentPosition
                }
            }
        }
    }

    fun initPlayer() {
        initializeVideoPlayer()
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

    fun onLifecycleChange(lifecycle: Lifecycle.Event) {
        when(lifecycle) {
            Lifecycle.Event.ON_PAUSE -> {
                player.playWhenReady = false
            }
            Lifecycle.Event.ON_RESUME -> {
                player.playWhenReady = true
            }
            else -> Unit
        }
    }

    fun updatePlayerState(playerView: PlayerView) {
        playerView.useController = false
        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        playerView.keepScreenOn = isVideoPlaying
    }

    fun onForward() {
        forwardCount += 1
        player.seekForward()
        logAnalyticsCountEvent(
            eventName = AppConstants.ANALYTICS_FORWARD,
            count = forwardCount
        )
    }

    fun onRewind() {
        backWardCount += 1
        player.seekBack()
        logAnalyticsCountEvent(
            eventName = AppConstants.ANALYTICS_BACKWARD,
            count = backWardCount
        )
    }

    fun onPlayPause() {
        isVideoPlaying = isVideoPlaying.not()
        player.playWhenReady = isVideoPlaying
        // only logs pause events skips play event
        if (isVideoPlaying.not()) {
            pauseCount += 1
            logAnalyticsCountEvent(
                eventName = AppConstants.ANALYTICS_PAUSE,
                count = pauseCount
            )
        }
    }

    fun onSeekTo(skip: Long) {
        player.seekTo(skip)
    }

    private fun logAnalyticsCountEvent(
        eventName: String,
        count: Int
    ) {
        analyticsService.logCountEvent(eventName, count)
    }

    override fun onCleared() {
        super.onCleared()
        player.removeListener(listener)
        player.release()
    }
}