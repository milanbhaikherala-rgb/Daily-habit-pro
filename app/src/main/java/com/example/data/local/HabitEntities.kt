package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.Habit
import com.example.data.model.HabitCompletion

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val icon: String,
    val colorHex: Long,
    val frequencyType: String,
    val targetDaysOfWeek: String,
    val targetValue: Int,
    val targetUnit: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false
) {
    fun toDomain(): Habit = Habit(
        id = id,
        name = name,
        category = category,
        icon = icon,
        colorHex = colorHex,
        frequencyType = frequencyType,
        targetDaysOfWeek = targetDaysOfWeek,
        targetValue = targetValue,
        targetUnit = targetUnit,
        createdAt = createdAt,
        isArchived = isArchived
    )

    companion object {
        fun fromDomain(habit: Habit): HabitEntity = HabitEntity(
            id = habit.id,
            name = habit.name,
            category = habit.category,
            icon = habit.icon,
            colorHex = habit.colorHex,
            frequencyType = habit.frequencyType,
            targetDaysOfWeek = habit.targetDaysOfWeek,
            targetValue = habit.targetValue,
            targetUnit = habit.targetUnit,
            createdAt = habit.createdAt,
            isArchived = habit.isArchived
        )
    }
}

@Entity(
    tableName = "habit_completions",
    indices = [
        Index(value = ["habitId", "dateString"], unique = true),
        Index(value = ["dateString"])
    ]
)
data class HabitCompletionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long,
    val dateString: String, // "YYYY-MM-DD"
    val completedAt: Long = System.currentTimeMillis(),
    val value: Int = 1
) {
    fun toDomain(): HabitCompletion = HabitCompletion(
        id = id,
        habitId = habitId,
        dateString = dateString,
        completedAt = completedAt,
        value = value
    )

    companion object {
        fun fromDomain(completion: HabitCompletion): HabitCompletionEntity = HabitCompletionEntity(
            id = completion.id,
            habitId = completion.habitId,
            dateString = completion.dateString,
            completedAt = completion.completedAt,
            value = completion.value
        )
    }
}
