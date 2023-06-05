package com.sample.player.video

import CountTextField
import LifecycleObserver
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.sample.player.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import noRippleClickable

@UnstableApi
@Composable
fun VideoPlayerScreen(
    modifier: Modifier = Modifier,
    videoPlayerViewModel: VideoPlayerViewModel = hiltViewModel()
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top
    ) {
        VideoPlayerView(
            player = videoPlayerViewModel.player,
            releasePLayer = videoPlayerViewModel::releasePlayer,
            initListener = videoPlayerViewModel::initPlayer,
            updatePlayerState = videoPlayerViewModel::updatePlayerState,
            onForwardClick = videoPlayerViewModel::onForward,
            onRewindClick = videoPlayerViewModel::onRewind,
            onPlayPauseClick = videoPlayerViewModel::onPlayPause,
            onSeekChanged = videoPlayerViewModel::onSeekTo,
            lifeCycleEvent = videoPlayerViewModel::onLifecycleChange,
            bufferPercentage = { videoPlayerViewModel.bufferedPercentage },
            isVideoPlaying = { videoPlayerViewModel.isVideoPlaying },
            totalDuration = { videoPlayerViewModel.totalDuration },
            currentTime = { videoPlayerViewModel.videoTimer }
        )
        CountFields(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            pauseCount = { videoPlayerViewModel.pauseCount },
            forwardCount = { videoPlayerViewModel.forwardCount },
            backwardCount = { videoPlayerViewModel.backWardCount }
        )
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun VideoPlayerView(
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

    var showPlayerControls by remember { mutableStateOf(true) }
    var seekBarTracking by remember { mutableStateOf(false) }

    LaunchedEffect(
        key1 = showPlayerControls,
        key2 = seekBarTracking
    ) {
        launch {
            if (showPlayerControls) {
                delay(5000)
                if (!seekBarTracking)
                    showPlayerControls = false
            }
        }
    }

    DisposableEffect(key1 = true) {
        initListener()
        onDispose {
            releasePLayer()
        }
    }

    Column(
        modifier = modifier
            .wrapContentHeight(),
        verticalArrangement = Arrangement.Top
    ) {
        Box(modifier = Modifier.noRippleClickable {
            showPlayerControls = !showPlayerControls
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
            AnimatedContent(
                modifier = Modifier.matchParentSize(),
                targetState = showPlayerControls,
            ) {
                if (it) {
                    Box {
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
                            },
                            onSeekChangeFinished = {
                                seekBarTracking = false
                            }
                        )
                    }
                }
            }
//            androidx.compose.animation.AnimatedVisibility(
//                modifier = Modifier.matchParentSize(),
//                visible = showPlayerControls,
//                enter = fadeIn(),
//                exit = fadeOut()
//            ) {
//                Box {
//                    PlayerCentreControls(
//                        onForwardClick = onForwardClick,
//                        onRewindClick = onRewindClick,
//                        onPlayPauseClick = onPlayPauseClick,
//                        isVideoPlaying = isVideoPlaying,
//                        modifier = Modifier.align(Alignment.Center)
//                    )
//                    PlayerBottomControls(
//                        modifier = Modifier.align(Alignment.BottomCenter),
//                        totalDuration = totalDuration,
//                        currentTime = currentTime,
//                        bufferPercentage = bufferPercentage,
//                        onSeekChanged = {
//                            seekBarTracking = true
//                            onSeekChanged(it.toLong())
//                        },
//                        onSeekChangeFinished = {
//                            seekBarTracking = false
//                        }
//                    )
//                }
//            }
        }
    }
}

@Composable
fun CountFields(
    modifier: Modifier = Modifier,
    pauseCount: () -> Int = { 0 },
    forwardCount: () -> Int = { 0 },
    backwardCount: () -> Int = { 0 },
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CountTextField(
            label = stringResource(R.string.pause_count) + ":",
            count = pauseCount()
        )
        CountTextField(
            label = stringResource(R.string.forward_count) + ":",
            count = forwardCount()
        )
        CountTextField(
            label = stringResource(R.string.backward_count) + ":",
            count = backwardCount()
        )
    }
}

@Preview(showBackground = true, device = Devices.NEXUS_5X)
@Composable
private fun ShowVideoScreenPreview() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        VideoPlayerView(
            modifier = Modifier.wrapContentHeight()
        )
        CountFields(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        )
    }
}