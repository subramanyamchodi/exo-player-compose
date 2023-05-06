package com.sample.player.analytics

interface AnalyticsService {

    fun logCountEvent(
        eventName: String,
        eventCount: Int
    )

}