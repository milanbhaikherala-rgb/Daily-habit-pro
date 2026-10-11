package com.example

import com.example.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DateUtilsTest {

    @Test
    fun testDateFormatAndGreeting() {
        val today = DateUtils.getTodayDateString()
        assertTrue(today.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
        val greeting = DateUtils.getGreeting()
        assertTrue(greeting.isNotBlank())
    }

    @Test
    fun testAppStreakCalculation() {
        val today = "2026-10-11"
        val activeDates = setOf("2026-10-11", "2026-10-10", "2026-10-09", "2026-10-08")
        val streak = DateUtils.calculateAppStreak(activeDates, today)
        assertEquals(4, streak)
    }

    @Test
    fun testBrokenAppStreakCalculation() {
        val today = "2026-10-11"
        // 2026-10-09 is missing
        val activeDates = setOf("2026-10-11", "2026-10-10", "2026-10-08")
        val streak = DateUtils.calculateAppStreak(activeDates, today)
        assertEquals(2, streak)
    }
}
