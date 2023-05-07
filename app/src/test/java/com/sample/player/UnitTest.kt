package com.sample.player

import formatMinSec
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class UnitTest {

    @Test
    fun time_conversion_isCorrect() {
        val timeInMs = (60 * 60 * 1000) // 1 hour
            .minus(2 * 60 * 1000) // 2 min
            .minus(45 * 1000) // 45 seconds
            .toLong()
        assertEquals("57:15", timeInMs.formatMinSec())
    }
}