package com.sample.player.di

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.upstream.DefaultAllocator
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.sample.player.AppConstants.MAX_BACKWARD_DURATION
import com.sample.player.AppConstants.MAX_BUFFER_DURATION
import com.sample.player.AppConstants.MAX_FORWARD_DURATION
import com.sample.player.AppConstants.MIN_BUFFER_DURATION
import com.sample.player.AppConstants.MIN_PLAYBACK_RESUME_BUFFER
import com.sample.player.AppConstants.MIN_PLAYBACK_START_BUFFER
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@UnstableApi
@Module
@InstallIn(SingletonComponent::class)
object AppProvideModule {

    @Provides
    @Singleton
    fun providesPlayer(
        @ApplicationContext context: Context
    ): Player {
        val loadControl = DefaultLoadControl.Builder()
            .setAllocator(DefaultAllocator(true, 16))
            .setBufferDurationsMs(
                MIN_BUFFER_DURATION,
                MAX_BUFFER_DURATION,
                MIN_PLAYBACK_START_BUFFER,
                MIN_PLAYBACK_RESUME_BUFFER
            )
            .setTargetBufferBytes(-1)
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        return ExoPlayer.Builder(context)
            .setSeekForwardIncrementMs(MAX_FORWARD_DURATION)
            .setSeekBackIncrementMs(MAX_BACKWARD_DURATION)
            .setLoadControl(loadControl)
            .build()
    }

    @Singleton
    @Provides
    fun providesAnalytics(): FirebaseAnalytics = Firebase.analytics


}