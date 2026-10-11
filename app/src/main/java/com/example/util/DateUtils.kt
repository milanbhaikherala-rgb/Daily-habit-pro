package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private const val DATE_FORMAT = "yyyy-MM-dd"

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.US)
        return sdf.format(Date())
    }

    fun formatDateForDisplay(dateString: String): String {
        return try {
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.US)
            val date = sdf.parse(dateString) ?: return dateString
            val displaySdf = SimpleDateFormat("EEEE, MMM d", Locale.US)
            displaySdf.format(date)
        } catch (_: Exception) {
            dateString
        }
    }

    fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Good Morning ☀️"
            in 12..16 -> "Good Afternoon 🌤️"
            in 17..21 -> "Good Evening 🌙"
            else -> "Good Night ⭐️"
        }
    }

    /**
     * Returns 1 for Monday, 2 for Tuesday, ..., 7 for Sunday (ISO standard)
     */
    fun getDayOfWeek(dateString: String): Int {
        return try {
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.US)
            val date = sdf.parse(dateString) ?: return 1
            val cal = Calendar.getInstance()
            cal.time = date
            // In Calendar: SUNDAY=1, MONDAY=2, ..., SATURDAY=7
            when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> 1
                Calendar.TUESDAY -> 2
                Calendar.WEDNESDAY -> 3
                Calendar.THURSDAY -> 4
                Calendar.FRIDAY -> 5
                Calendar.SATURDAY -> 6
                Calendar.SUNDAY -> 7
                else -> 1
            }
        } catch (_: Exception) {
            1
        }
    }

    fun getDayAbbr(dateString: String): String {
        return try {
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.US)
            val date = sdf.parse(dateString) ?: return ""
            val out = SimpleDateFormat("EEE", Locale.US)
            out.format(date)
        } catch (_: Exception) {
            ""
        }
    }

    fun getPreviousDate(dateString: String): String {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.US)
        val date = sdf.parse(dateString) ?: Date()
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return sdf.format(cal.time)
    }

    /**
     * Get past N days up to today (including today), chronologically ordered
     */
    fun getPastDays(daysCount: Int, endDateString: String = getTodayDateString()): List<String> {
        val list = mutableListOf<String>()
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.US)
        val endDate = sdf.parse(endDateString) ?: Date()
        val cal = Calendar.getInstance()
        cal.time = endDate
        
        for (i in 0 until daysCount) {
            list.add(sdf.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        return list.reversed()
    }

    /**
     * Checks if a habit is scheduled for the given date
     */
    fun isHabitScheduledFor(frequencyType: String, targetDaysOfWeek: String, dateString: String): Boolean {
        if (frequencyType == "DAILY") return true
        val day = getDayOfWeek(dateString)
        val allowedDays = targetDaysOfWeek.split(",").mapNotNull { it.trim().toIntOrNull() }
        return allowedDays.contains(day)
    }

    /**
     * Calculates consecutive days streak for a specific habit
     */
    fun calculateHabitStreak(
        frequencyType: String,
        targetDaysOfWeek: String,
        completedDates: Set<String>,
        todayDateString: String
    ): Int {
        var streak = 0
        var checkDate = todayDateString

        // If today is completed, count it, else start checking from yesterday
        val isTodayScheduled = isHabitScheduledFor(frequencyType, targetDaysOfWeek, todayDateString)
        if (isTodayScheduled && completedDates.contains(todayDateString)) {
            streak++
            checkDate = getPreviousDate(todayDateString)
        } else if (!isTodayScheduled) {
            // Today wasn't scheduled, start looking from yesterday
            checkDate = getPreviousDate(todayDateString)
        } else {
            // Today is scheduled but not completed yet; streak is based on yesterday
            checkDate = getPreviousDate(todayDateString)
        }

        // Loop back up to 365 days
        var loopCount = 0
        while (loopCount < 365) {
            val isScheduled = isHabitScheduledFor(frequencyType, targetDaysOfWeek, checkDate)
            if (isScheduled) {
                if (completedDates.contains(checkDate)) {
                    streak++
                } else {
                    // Broken streak
                    break
                }
            }
            // If not scheduled on that day (e.g. weekdays only), streak doesn't break, skip to previous day
            checkDate = getPreviousDate(checkDate)
            loopCount++
        }
        return streak
    }

    /**
     * Calculates overall App Streak: consecutive days where user completed at least 1 habit
     */
    fun calculateAppStreak(activeDatesWithAtLeastOneDone: Set<String>, todayDateString: String): Int {
        var streak = 0
        var checkDate = todayDateString

        if (activeDatesWithAtLeastOneDone.contains(todayDateString)) {
            streak++
            checkDate = getPreviousDate(todayDateString)
        } else {
            checkDate = getPreviousDate(todayDateString)
        }

        var loopCount = 0
        while (loopCount < 365) {
            if (activeDatesWithAtLeastOneDone.contains(checkDate)) {
                streak++
                checkDate = getPreviousDate(checkDate)
            } else {
                break
            }
            loopCount++
        }
        return streak
    }
}
