package com.sample.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.media3.common.util.UnstableApi
import com.sample.player.ui.theme.VideoPlayerAppTheme
import com.sample.player.video.VideoPlayerScreen
import dagger.hilt.android.AndroidEntryPoint

@UnstableApi
@AndroidEntryPoint
class VideoPlayActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoPlayerAppTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(text = getString(R.string.video_play))
                            },
                            backgroundColor = MaterialTheme.colors.primary
                        )
                    }
                ) {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        VideoPlayerScreen(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = Color.White)
                        )
                    }
                }

            }
        }
    }
}