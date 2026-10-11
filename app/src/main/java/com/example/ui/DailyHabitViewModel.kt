package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.QuotesProvider
import com.example.data.local.DailyHabitDatabase
import com.example.data.model.AchievementBadge
import com.example.data.model.DailyProgress
import com.example.data.model.DailyQuote
import com.example.data.model.DayCompletionTrend
import com.example.data.model.Habit
import com.example.data.model.HabitCompletion
import com.example.data.model.HabitItemUiState
import com.example.data.model.HeatmapCell
import com.example.data.repository.HabitRepository
import com.example.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DailyHabitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HabitRepository

    init {
        val db = DailyHabitDatabase.getDatabase(application)
        repository = HabitRepository(db.habitDao())
    }

    private val _selectedDate = MutableStateFlow(DateUtils.getTodayDateString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow("All")
    val selectedCategoryFilter: StateFlow<String> = _selectedCategoryFilter.asStateFlow()

    private val _quoteOffset = MutableStateFlow(0)
    val quoteOffset: StateFlow<Int> = _quoteOffset.asStateFlow()

    private val _confettiTrigger = MutableStateFlow(0L)
    val confettiTrigger: StateFlow<Long> = _confettiTrigger.asStateFlow()

    val allHabits: StateFlow<List<Habit>> = repository.allHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCompletions: StateFlow<List<HabitCompletion>> = repository.allCompletions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Check if initial starter pack should be preloaded on first run
    init {
        viewModelScope.launch {
            allHabits.collect { list ->
                if (list.isEmpty()) {
                    repository.preloadStarterHabits()
                }
            }
        }
    }

    // Daily quote for today
    val currentQuote: StateFlow<DailyQuote> = combine(_selectedDate, _quoteOffset) { date, offset ->
        QuotesProvider.getQuoteForDate(date, offset)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        QuotesProvider.getQuoteForDate(DateUtils.getTodayDateString())
    )

    // Habit Item list for current selected date & category filter
    val habitItems: StateFlow<List<HabitItemUiState>> = combine(
        allHabits,
        allCompletions,
        _selectedDate,
        _selectedCategoryFilter
    ) { habits, completions, date, categoryFilter ->
        val completionsByHabit = completions.groupBy { it.habitId }
        val todayStr = DateUtils.getTodayDateString()

        val scheduledHabits = habits.filter { habit ->
            // Filter by scheduled day
            DateUtils.isHabitScheduledFor(habit.frequencyType, habit.targetDaysOfWeek, date)
        }

        val filteredByCategory = if (categoryFilter == "All") {
            scheduledHabits
        } else {
            scheduledHabits.filter { it.category.equals(categoryFilter, ignoreCase = true) }
        }

        filteredByCategory.map { habit ->
            val habitCompletions = completionsByHabit[habit.id] ?: emptyList()
            val completedDatesSet = habitCompletions.map { it.dateString }.toSet()
            val isDone = completedDatesSet.contains(date)
            val streak = DateUtils.calculateHabitStreak(
                frequencyType = habit.frequencyType,
                targetDaysOfWeek = habit.targetDaysOfWeek,
                completedDates = completedDatesSet,
                todayDateString = todayStr
            )

            HabitItemUiState(
                habit = habit,
                isCompletedToday = isDone,
                currentStreak = streak,
                completionsThisWeek = habitCompletions.count {
                    DateUtils.getPastDays(7, todayStr).contains(it.dateString)
                },
                totalCompletions = habitCompletions.size
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Overall daily progress & streak summary
    val dailyProgress: StateFlow<DailyProgress> = combine(
        allHabits,
        allCompletions,
        _selectedDate
    ) { habits, completions, date ->
        val scheduledToday = habits.filter {
            DateUtils.isHabitScheduledFor(it.frequencyType, it.targetDaysOfWeek, date)
        }
        val completedTodayIds = completions
            .filter { it.dateString == date }
            .map { it.habitId }
            .toSet()

        val completedCount = scheduledToday.count { completedTodayIds.contains(it.id) }
        val totalCount = scheduledToday.size
        val percentage = if (totalCount > 0) ((completedCount.toFloat() / totalCount) * 100).toInt() else 0

        val activeDatesWithCompletions = completions.map { it.dateString }.toSet()
        val appStreak = DateUtils.calculateAppStreak(
            activeDatesWithAtLeastOneDone = activeDatesWithCompletions,
            todayDateString = DateUtils.getTodayDateString()
        )

        DailyProgress(
            totalHabitsToday = totalCount,
            completedHabitsToday = completedCount,
            completionPercentage = percentage,
            totalAppStreak = appStreak
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DailyProgress(0, 0, 0, 0)
    )

    // Weekly trend (Last 7 days bar chart data)
    val weeklyTrends: StateFlow<List<DayCompletionTrend>> = combine(
        allHabits,
        allCompletions
    ) { habits, completions ->
        val past7Days = DateUtils.getPastDays(7, DateUtils.getTodayDateString())
        val completionsByDate = completions.groupBy { it.dateString }

        past7Days.map { dateStr ->
            val scheduled = habits.filter {
                DateUtils.isHabitScheduledFor(it.frequencyType, it.targetDaysOfWeek, dateStr)
            }
            val doneCount = completionsByDate[dateStr]?.size ?: 0
            val totalSched = if (scheduled.isNotEmpty()) scheduled.size else 1
            val ratio = (doneCount.toFloat() / totalSched).coerceIn(0f, 1f)

            DayCompletionTrend(
                dateString = dateStr,
                dayLabel = DateUtils.getDayAbbr(dateStr),
                totalScheduled = scheduled.size,
                totalCompleted = doneCount,
                completionRatio = ratio
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Activity Heatmap (Last 28 days grid)
    val heatmapCells: StateFlow<List<HeatmapCell>> = combine(
        allHabits,
        allCompletions
    ) { habits, completions ->
        val pastDays = DateUtils.getPastDays(28, DateUtils.getTodayDateString())
        val completionsByDate = completions.groupBy { it.dateString }
        val today = DateUtils.getTodayDateString()

        pastDays.map { dateStr ->
            val scheduled = habits.filter {
                DateUtils.isHabitScheduledFor(it.frequencyType, it.targetDaysOfWeek, dateStr)
            }
            val doneCount = completionsByDate[dateStr]?.size ?: 0
            val scheduledCount = if (scheduled.isNotEmpty()) scheduled.size else 1
            val ratio = (doneCount.toFloat() / scheduledCount).coerceIn(0f, 1f)
            val dayNum = dateStr.substringAfterLast("-").toIntOrNull() ?: 1

            HeatmapCell(
                dateString = dateStr,
                dayOfMonth = dayNum,
                completionRatio = ratio,
                isToday = dateStr == today,
                isFuture = false,
                completedCount = doneCount
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Gamification & Badges
    val badges: StateFlow<List<AchievementBadge>> = combine(
        allHabits,
        allCompletions,
        dailyProgress
    ) { habits, completions, progress ->
        val streak = progress.totalAppStreak
        val totalCompletions = completions.size
        val totalHabits = habits.size

        val hasPerfectDay = completions.groupBy { it.dateString }.any { (date, doneList) ->
            val sched = habits.filter { DateUtils.isHabitScheduledFor(it.frequencyType, it.targetDaysOfWeek, date) }
            sched.size >= 3 && doneList.size >= sched.size
        }

        listOf(
            AchievementBadge(
                id = "first_step",
                title = "First Step",
                description = "Complete your first habit",
                emoji = "🌱",
                requirementText = "1 completion",
                isUnlocked = totalCompletions >= 1,
                progress = totalCompletions.coerceAtMost(1),
                maxProgress = 1
            ),
            AchievementBadge(
                id = "starter_3",
                title = "3 Day Starter",
                description = "Maintain a 3-day app streak",
                emoji = "🔥",
                requirementText = "3 active days",
                isUnlocked = streak >= 3,
                progress = streak.coerceAtMost(3),
                maxProgress = 3
            ),
            AchievementBadge(
                id = "master_7",
                title = "7 Day Master",
                description = "Build momentum with 7 consecutive days",
                emoji = "⚡",
                requirementText = "7 active days",
                isUnlocked = streak >= 7,
                progress = streak.coerceAtMost(7),
                maxProgress = 7
            ),
            AchievementBadge(
                id = "legend_10",
                title = "10 Day Legend",
                description = "Double digits! Reach a 10-day streak",
                emoji = "🏆",
                requirementText = "10 active days",
                isUnlocked = streak >= 10,
                progress = streak.coerceAtMost(10),
                maxProgress = 10
            ),
            AchievementBadge(
                id = "titan_30",
                title = "30 Day Titan",
                description = "Habits become a lifestyle in 30 days",
                emoji = "👑",
                requirementText = "30 active days",
                isUnlocked = streak >= 30,
                progress = streak.coerceAtMost(30),
                maxProgress = 30
            ),
            AchievementBadge(
                id = "perfect_day",
                title = "Perfect Day",
                description = "Complete 100% of all habits on a single day",
                emoji = "⭐",
                requirementText = "100% completed day",
                isUnlocked = hasPerfectDay,
                progress = if (hasPerfectDay) 1 else 0,
                maxProgress = 1
            ),
            AchievementBadge(
                id = "century_club",
                title = "Century Club",
                description = "Log 100 total habit completions",
                emoji = "💯",
                requirementText = "100 completions",
                isUnlocked = totalCompletions >= 100,
                progress = totalCompletions.coerceAtMost(100),
                maxProgress = 100
            ),
            AchievementBadge(
                id = "pioneer_5",
                title = "Habit Pioneer",
                description = "Create and track at least 5 distinct habits",
                emoji = "🚀",
                requirementText = "5 active habits",
                isUnlocked = totalHabits >= 5,
                progress = totalHabits.coerceAtMost(5),
                maxProgress = 5
            )
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User Actions
    fun toggleHabit(habitId: Long) {
        viewModelScope.launch {
            val date = _selectedDate.value
            val isDone = allCompletions.value.any { it.habitId == habitId && it.dateString == date }
            val nowCompleted = repository.toggleHabitCompletion(habitId, date, isDone)
            if (nowCompleted) {
                // Trigger celebratory confetti burst
                _confettiTrigger.value = System.currentTimeMillis()
            }
        }
    }

    fun saveHabit(habit: Habit) {
        viewModelScope.launch {
            repository.saveHabit(habit)
        }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
        }
    }

    fun setCategoryFilter(category: String) {
        _selectedCategoryFilter.value = category
    }

    fun refreshQuote() {
        _quoteOffset.value += 1
    }

    fun setSelectedDate(dateString: String) {
        _selectedDate.value = dateString
    }

    fun clearConfetti() {
        _confettiTrigger.value = 0L
    }

    fun preloadStarters() {
        viewModelScope.launch {
            repository.preloadStarterHabits()
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }
}
