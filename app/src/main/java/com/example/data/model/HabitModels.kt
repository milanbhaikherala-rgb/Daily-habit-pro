package com.example.data.model

data class Habit(
    val id: Long = 0,
    val name: String,
    val category: String,
    val icon: String,
    val colorHex: Long,
    val frequencyType: String = "DAILY", // "DAILY", "SPECIFIC_DAYS"
    val targetDaysOfWeek: String = "1,2,3,4,5,6,7", // 1=Mon, 2=Tue, ..., 7=Sun
    val targetValue: Int = 1,
    val targetUnit: String = "times",
    val createdAt: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false
)

data class HabitCompletion(
    val id: Long = 0,
    val habitId: Long,
    val dateString: String, // "YYYY-MM-DD"
    val completedAt: Long = System.currentTimeMillis(),
    val value: Int = 1
)

data class HabitItemUiState(
    val habit: Habit,
    val isCompletedToday: Boolean,
    val currentStreak: Int,
    val completionsThisWeek: Int,
    val totalCompletions: Int
)

data class DailyProgress(
    val totalHabitsToday: Int,
    val completedHabitsToday: Int,
    val completionPercentage: Int, // 0 - 100
    val totalAppStreak: Int
)

data class DayCompletionTrend(
    val dateString: String,
    val dayLabel: String, // "Mon", "Tue", etc.
    val totalScheduled: Int,
    val totalCompleted: Int,
    val completionRatio: Float // 0f - 1f
)

data class HeatmapCell(
    val dateString: String,
    val dayOfMonth: Int,
    val completionRatio: Float, // 0f to 1f
    val isToday: Boolean,
    val isFuture: Boolean,
    val completedCount: Int
)

data class AchievementBadge(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val requirementText: String,
    val isUnlocked: Boolean,
    val progress: Int,
    val maxProgress: Int
)

data class DailyQuote(
    val text: String,
    val author: String
)
