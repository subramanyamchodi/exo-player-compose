package com.sample.player.di

import com.sample.player.analytics.AnalyticsService
import com.sample.player.analytics.AnalyticsServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AppBindModule {

    @Binds
    fun bindsAnalyticsService(analyticsService: AnalyticsServiceImpl): AnalyticsService
}