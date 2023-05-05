package com.sample.player.video

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.ui.PlayerView

@Composable
fun VideoPlayerScreen(
    videoPlayerViewModel: VideoPlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LifecycleObserver { _, event -> videoPlayerViewModel.lifecycleEvent = event }
    Box(modifier = Modifier.fillMaxSize()
        .background(color = Color.Black)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize()
                .align(Alignment.Center),
            factory = {
                PlayerView(context).also {
                    it.player = videoPlayerViewModel.player
                }
            },
            update = videoPlayerViewModel::updatePlayerState
        )

    }
}

@Composable
fun LifecycleObserver(
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent: (LifecycleOwner, Lifecycle.Event) -> Unit
) {
    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver(onEvent)
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }
}