package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyProgress
import com.example.data.model.DailyQuote
import com.example.data.model.Habit
import com.example.data.model.HabitItemUiState
import com.example.ui.components.HabitCard
import com.example.ui.components.QuoteBanner
import com.example.ui.theme.AmberStreak
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletSecondary
import com.example.util.DateUtils

@Composable
fun HomeScreen(
    selectedDate: String,
    dailyProgress: DailyProgress,
    currentQuote: DailyQuote,
    habitItems: List<HabitItemUiState>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onToggleHabit: (Long) -> Unit,
    onEditHabit: (Habit) -> Unit,
    onDeleteHabit: (Long) -> Unit,
    onAddHabitClick: () -> Unit,
    onRefreshQuote: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val greeting = remember { DateUtils.getGreeting() }
    val formattedDate = remember(selectedDate) { DateUtils.formatDateForDisplay(selectedDate) }

    val categories = listOf("All", "Health", "Productivity", "Mindfulness", "Fitness", "Work")

    val animatedProgress by animateFloatAsState(
        targetValue = if (dailyProgress.totalHabitsToday > 0) {
            dailyProgress.completedHabitsToday.toFloat() / dailyProgress.totalHabitsToday
        } else 0f,
        label = "daily_progress_anim"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("home_screen_list"),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = contentPadding.calculateTopPadding() + 8.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Top Header with Date, Dynamic Greeting, and App Streak Summary
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = formattedDate.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = greeting,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                    }

                    // Total App Streak Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF2E1A0C),
                                        Color(0xFF381E10)
                                    )
                                )
                            )
                            .border(1.dp, AmberStreak.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("app_streak_pill"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Total Streak",
                                tint = AmberStreak,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "${dailyProgress.totalAppStreak} Days",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AmberStreak
                            )
                        }
                    }
                }
            }
        }

        // 2. Motivational Quote Banner
        item {
            QuoteBanner(
                quote = currentQuote,
                onRefreshQuote = onRefreshQuote
            )
        }

        // 3. Daily Completion Progress Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_progress_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Today's Progress",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${dailyProgress.completedHabitsToday} of ${dailyProgress.totalHabitsToday} habits completed",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }

                        // Percentage badge
                        Text(
                            text = "${dailyProgress.completionPercentage}%",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (dailyProgress.completionPercentage == 100) EmeraldLight else EmeraldPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = EmeraldPrimary,
                        trackColor = Color(0xFF1E293B)
                    )

                    if (dailyProgress.totalHabitsToday > 0 && dailyProgress.completedHabitsToday == dailyProgress.totalHabitsToday) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "All habits crushed for today! Keep the momentum! 🎉",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldLight,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // 4. Quick Action Button & Category Filters Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Habits",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Button(
                        onClick = onAddHabitClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = Color(0xFF022C22)
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("quick_add_habit_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add Habit",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category Chips Scroll
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) EmeraldPrimary else DarkSurfaceElevated)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) EmeraldPrimary else DarkBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onCategorySelected(cat) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("filter_chip_$cat"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF022C22) else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // 5. Habits List
        if (habitItems.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "🌱",
                            fontSize = 44.sp
                        )
                        Text(
                            text = if (selectedCategory == "All") "No habits scheduled for today" else "No $selectedCategory habits yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Tap 'Add Habit' above to start your daily streak!",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            items(items = habitItems, key = { it.habit.id }) { itemState ->
                HabitCard(
                    habitState = itemState,
                    onToggleCompletion = { onToggleHabit(itemState.habit.id) },
                    onEditHabit = { onEditHabit(itemState.habit) },
                    onDeleteHabit = { onDeleteHabit(itemState.habit.id) }
                )
            }
        }
    }
}
