package com.sample.player.video

import LifecycleObserver
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.sample.player.R
import formatMinSec
import kotlinx.coroutines.delay

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
    initListener: () -> Unit = {},
    releasePLayer: () -> Unit = {},
    lifeCycleEvent: ((_: Lifecycle.Event) -> Unit)? = null,
    updatePlayerState: ((_: PlayerView) -> Unit)? = null
) {
    val context = LocalContext.current

    LifecycleObserver { _, event -> lifeCycleEvent?.invoke(event) }

    var isPlaying by remember { mutableStateOf(true) }
    var showOptions by remember { mutableStateOf(true) }
    var seekBar by remember { mutableStateOf(0f) }

    LaunchedEffect(key1 = showOptions, key2 = seekBar) {
        if (showOptions) {
            delay(3000)
            showOptions = false
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
                PlayerControls(
                    onForwardClick = {
                        player?.seekForward()
                    },
                    onRewindClick = {
                        player?.seekBack()
                    },
                    onPlayPauseClick = {
                        isPlaying = isPlaying.not()
                        player?.playWhenReady = isPlaying
                    },
                    isVideoPlaying = { isPlaying },
                    modifier = Modifier.align(Alignment.Center)
                )
                BottomControls(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    isVideoPlaying = { isPlaying },
                    totalDuration = totalDuration,
                    currentTime = currentTime,
                    bufferPercentage = { player?.bufferedPercentage ?: 0 },
                    onSeekChanged = {
                        player?.seekTo(it.toLong())
                    }
                )
            }
        }
    }
}

@Composable
fun PlayerControls(
    modifier: Modifier = Modifier,
    onForwardClick: () -> Unit,
    onRewindClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    isVideoPlaying: () -> Boolean
) {
    //black overlay across the video player
    Box(modifier = modifier.background(Color.Transparent)) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            //replay button
            IconButton(modifier = Modifier, onClick = onRewindClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_replay),
                    contentDescription = "Replay 5 seconds",
                    modifier = Modifier.size(36.dp),
                    tint = Color.White
                )
            }

            //pause/play toggle button
            IconButton(modifier = Modifier, onClick = onPlayPauseClick) {
                Icon(
                    painter = painterResource(
                        id = if (isVideoPlaying()) R.drawable.ic_pause else R.drawable.ic_play
                    ),
                    contentDescription = "Replay 5 seconds",
                    modifier = Modifier.size(36.dp),
                    tint = Color.White
                )
            }

            //forward button
            IconButton(modifier = Modifier, onClick = onForwardClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_forward),
                    contentDescription = "Forward 5 seconds",
                    modifier = Modifier.size(36.dp),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun BottomControls(
    modifier: Modifier = Modifier,
    isVideoPlaying: () -> Boolean,
    totalDuration: () -> Long,
    currentTime: () -> Long,
    bufferPercentage: () -> Int,
    onSeekChanged: (timeMs: Float) -> Unit
) {

    val duration = remember(totalDuration()) { totalDuration() }

    val videoTime = remember(currentTime()) { currentTime() }

    val buffer = remember(bufferPercentage()) { bufferPercentage() }

    Column(modifier = modifier) {
        // seek bar
        Slider(
            modifier = Modifier.fillMaxWidth(),
            value = videoTime.toFloat(),
            onValueChange = onSeekChanged,
            valueRange = if (duration > 0) 0f..duration.toFloat() else 0f..0f,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTickColor = Color.White,
                activeTrackColor = Color.White
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            // show total video time / current video time
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "${duration.formatMinSec()} / ${videoTime.formatMinSec()}",
                color = Color.White,
                fontSize = 12.sp
            )
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