package com.example.data.repository

import com.example.data.local.HabitCompletionEntity
import com.example.data.local.HabitDao
import com.example.data.local.HabitEntity
import com.example.data.model.Habit
import com.example.data.model.HabitCompletion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HabitRepository(private val habitDao: HabitDao) {

    val allHabits: Flow<List<Habit>> = habitDao.getAllActiveHabits().map { entities ->
        entities.map { it.toDomain() }
    }

    val allCompletions: Flow<List<HabitCompletion>> = habitDao.getAllCompletions().map { entities ->
        entities.map { it.toDomain() }
    }

    fun getCompletionsForDate(dateString: String): Flow<List<HabitCompletion>> =
        habitDao.getCompletionsForDate(dateString).map { entities ->
            entities.map { it.toDomain() }
        }

    fun getCompletionsBetween(startDate: String, endDate: String): Flow<List<HabitCompletion>> =
        habitDao.getCompletionsBetween(startDate, endDate).map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun saveHabit(habit: Habit): Long {
        return if (habit.id == 0L) {
            habitDao.insertHabit(HabitEntity.fromDomain(habit))
        } else {
            habitDao.updateHabit(HabitEntity.fromDomain(habit))
            habit.id
        }
    }

    suspend fun deleteHabit(habitId: Long) {
        habitDao.deleteHabit(habitId)
        habitDao.deleteCompletionsForHabit(habitId)
    }

    suspend fun toggleHabitCompletion(habitId: Long, dateString: String, isCurrentlyCompleted: Boolean): Boolean {
        if (isCurrentlyCompleted) {
            habitDao.deleteCompletion(habitId, dateString)
            return false
        } else {
            habitDao.insertCompletion(
                HabitCompletionEntity(
                    habitId = habitId,
                    dateString = dateString,
                    completedAt = System.currentTimeMillis(),
                    value = 1
                )
            )
            return true
        }
    }

    suspend fun preloadStarterHabits() {
        val starters = listOf(
            Habit(
                name = "Drink 2L Water",
                category = "Health",
                icon = "💧",
                colorHex = 0xFF06B6D4,
                frequencyType = "DAILY",
                targetDaysOfWeek = "1,2,3,4,5,6,7",
                targetValue = 8,
                targetUnit = "glasses"
            ),
            Habit(
                name = "Morning Workout",
                category = "Fitness",
                icon = "🏃",
                colorHex = 0xFF10B981,
                frequencyType = "DAILY",
                targetDaysOfWeek = "1,2,3,4,5,6,7",
                targetValue = 30,
                targetUnit = "minutes"
            ),
            Habit(
                name = "Read 15 mins",
                category = "Productivity",
                icon = "📚",
                colorHex = 0xFF8B5CF6,
                frequencyType = "DAILY",
                targetDaysOfWeek = "1,2,3,4,5,6,7",
                targetValue = 15,
                targetUnit = "mins"
            ),
            Habit(
                name = "Mindful Meditation",
                category = "Mindfulness",
                icon = "🧘",
                colorHex = 0xFF6366F1,
                frequencyType = "DAILY",
                targetDaysOfWeek = "1,2,3,4,5,6,7",
                targetValue = 10,
                targetUnit = "mins"
            ),
            Habit(
                name = "Plan Tomorrow's Priorities",
                category = "Productivity",
                icon = "🎯",
                colorHex = 0xFFF59E0B,
                frequencyType = "DAILY",
                targetDaysOfWeek = "1,2,3,4,5,6,7",
                targetValue = 1,
                targetUnit = "time"
            )
        )
        habitDao.insertHabits(starters.map { HabitEntity.fromDomain(it) })
    }

    suspend fun clearAllData() {
        habitDao.clearAllHabits()
        habitDao.clearAllCompletions()
    }
}
