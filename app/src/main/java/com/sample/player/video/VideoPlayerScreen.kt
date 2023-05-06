package com.sample.player.video

import LifecycleObserver
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@UnstableApi
@Composable
fun VideoPlayerScreen(
    modifier: Modifier = Modifier,
    videoPlayerViewModel: VideoPlayerViewModel = hiltViewModel()
) {
    VideoPlayerView(
        modifier = modifier,
        player = videoPlayerViewModel.player,
        releasePLayer = videoPlayerViewModel::releasePlayer,
        initListener = videoPlayerViewModel::initListener,
        updatePlayerState = videoPlayerViewModel::updatePlayerState,
        onForwardClick = videoPlayerViewModel::onForward,
        onRewindClick = videoPlayerViewModel::onRewind,
        onPlayPauseClick = videoPlayerViewModel::onPlayPause,
        onSeekChanged = videoPlayerViewModel::onSeekTo,
        bufferPercentage = { videoPlayerViewModel.bufferedPercentage },
        isVideoPlaying = { videoPlayerViewModel.isVideoPlaying },
        totalDuration = { videoPlayerViewModel.totalDuration },
        currentTime = { videoPlayerViewModel.videoTimer },
        lifeCycleEvent = {
            videoPlayerViewModel.lifecycleEvent = it
        }
    )
}

@Composable
private fun VideoPlayerView(
    modifier: Modifier = Modifier,
    player: Player? = null,
    totalDuration: () -> Long = { 0L },
    currentTime: () -> Long = { 0L },
    bufferPercentage: () -> Int = { 0 },
    initListener: () -> Unit = {},
    releasePLayer: () -> Unit = {},
    onPlayPauseClick: () -> Unit = {},
    onForwardClick: () -> Unit = {},
    onRewindClick: () -> Unit = {},
    onSeekChanged: (timeMs: Long) -> Unit = {},
    isVideoPlaying: () -> Boolean = { false },
    lifeCycleEvent: ((_: Lifecycle.Event) -> Unit)? = null,
    updatePlayerState: ((_: PlayerView) -> Unit)? = null
) {
    val context = LocalContext.current

    LifecycleObserver { _, event -> lifeCycleEvent?.invoke(event) }

    var showOptions by remember { mutableStateOf(true) }
    var seekBarTracking by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = showOptions, key2 = seekBarTracking) {
        launch {
            if (showOptions) {
                delay(5000)
                if (!seekBarTracking)
                    showOptions = false
            }
        }
        launch {
            if (seekBarTracking) {
                delay(2000)
                seekBarTracking = false
            }
        }

    }

    DisposableEffect(key1 = true,) {
        initListener()
        onDispose {
            releasePLayer()
        }
    }

    Column(
        modifier = modifier
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Top
    ) {
        Box(modifier = Modifier.clickable {
            showOptions = true
        }) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fraction = 0.35f)
                    .background(color = Color.Black),
                factory = {
                    PlayerView(context).also {
                        it.player = player
                    }
                },
                update = {
                    updatePlayerState?.invoke(it)
                }
            )
            if (showOptions) {
                PlayerCentreControls(
                    onForwardClick = onForwardClick,
                    onRewindClick = onRewindClick,
                    onPlayPauseClick = onPlayPauseClick,
                    isVideoPlaying = isVideoPlaying,
                    modifier = Modifier.align(Alignment.Center)
                )
                PlayerBottomControls(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    totalDuration = totalDuration,
                    currentTime = currentTime,
                    bufferPercentage = bufferPercentage,
                    onSeekChanged = {
                        seekBarTracking = true
                        onSeekChanged(it.toLong())
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.NEXUS_5X)
@Composable
private fun ShowVideoScreenPreview() {
    VideoPlayerView(
        modifier = Modifier.fillMaxSize()
    )
}