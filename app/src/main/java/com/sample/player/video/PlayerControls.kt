package com.sample.player.video

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sample.player.R
import formatMinSec

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PlayerCentreControls(
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
            AnimatedContent(targetState = isVideoPlaying()) {
                IconButton(modifier = Modifier, onClick = onPlayPauseClick) {
                    Icon(
                        painter = painterResource(
                            id = if (it) R.drawable.ic_pause else R.drawable.ic_play
                        ),
                        contentDescription = "Replay 5 seconds",
                        modifier = Modifier.size(36.dp),
                        tint = Color.White
                    )
                }
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
fun PlayerBottomControls(
    modifier: Modifier = Modifier,
    totalDuration: () -> Long,
    currentTime: () -> Long,
    bufferPercentage: () -> Int,
    onSeekChanged: (timeMs: Float) -> Unit
) {

    val duration = remember(totalDuration()) { totalDuration() }

    val videoTime = remember(currentTime()) { currentTime() }

    val buffer = remember(bufferPercentage()) { bufferPercentage() }

    Column(modifier = modifier) {
        Box(modifier = Modifier) {
            Slider(
                modifier = Modifier.fillMaxWidth(),
                value = buffer.toFloat(),
                enabled = false,
                onValueChange = { /*onSeekChanged*/ },
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    disabledThumbColor = Color.Transparent,
                    disabledActiveTrackColor = Color.White.copy(alpha = 0.5f),
                )
            )
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
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
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