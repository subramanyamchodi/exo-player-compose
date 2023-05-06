package com.sample.player.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import javax.inject.Inject

class AnalyticsServiceImpl @Inject constructor(
    private val analytics: FirebaseAnalytics
): AnalyticsService {

    override fun logCountEvent(eventName: String, eventCount: Int) {
        analytics.logEvent(eventName) {
            param("count", "$eventCount")
        }
    }

}