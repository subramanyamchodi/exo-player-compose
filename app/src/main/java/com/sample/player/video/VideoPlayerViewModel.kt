package com.sample.player.video

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    val player: Player
): ViewModel() {

    private val videoUrl1 = "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd"
    private val videoUrl = "https://player.vimeo.com/external/456092365.hd.mp4?s=2a38eeb7ba9baa0ff226af4943a13367a0f69a64&profile_id=172&oauth2_token_id=57447761"
    var lifecycleEvent by mutableStateOf(Lifecycle.Event.ON_CREATE)

    private fun initializeVideoPlayer(url: String = videoUrl1) {
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.playWhenReady  = true
        player.prepare()
    }

    fun updatePlayerState(playerView: PlayerView) {
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
        player.release()
    }
}