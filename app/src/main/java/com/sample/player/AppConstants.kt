package com.sample.player

object AppConstants {
    const val MIN_BUFFER_DURATION = 3000 // 3 seconds
    const val MAX_BUFFER_DURATION = 8000 // 8 seconds
    const val MIN_PLAYBACK_RESUME_BUFFER = 1500 // 1.5 seconds
    const val MIN_PLAYBACK_START_BUFFER = 2000 // 2 seconds
    const val MAX_FORWARD_DURATION = 10000L
    const val MAX_BACKWARD_DURATION = 10000L

    //Analytics constants
    const val ANALYTICS_FORWARD = "forward_count"
    const val ANALYTICS_BACKWARD = "backward_count"
    const val ANALYTICS_PAUSE = "pause_count"
}