package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyProgress
import com.example.data.model.DayCompletionTrend
import com.example.data.model.Habit
import com.example.data.model.HabitCompletion
import com.example.data.model.HeatmapCell
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnalyticsScreen(
    dailyProgress: DailyProgress,
    weeklyTrends: List<DayCompletionTrend>,
    heatmapCells: List<HeatmapCell>,
    allHabits: List<Habit>,
    allCompletions: List<HabitCompletion>,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val animatedPercent by animateFloatAsState(
        targetValue = dailyProgress.completionPercentage.toFloat(),
        label = "analytics_percent_anim"
    )

    // Category breakdown calculation
    val habitMap = remember(allHabits) { allHabits.associateBy { it.id } }
    val categoryCompletions = remember(allCompletions, habitMap) {
        val map = mutableMapOf<String, Int>()
        allCompletions.forEach { completion ->
            val cat = habitMap[completion.habitId]?.category ?: "Other"
            map[cat] = (map[cat] ?: 0) + 1
        }
        map.toList().sortedByDescending { it.second }
    }
    val totalCompletionsCount = remember(categoryCompletions) {
        categoryCompletions.sumOf { it.second }.coerceAtLeast(1)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("analytics_screen"),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = contentPadding.calculateTopPadding() + 8.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        item {
            Column {
                Text(
                    text = "ANALYTICS & PROGRESS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = EmeraldLight
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Habit Insights",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
            }
        }

        // 1. Overall Daily Completion Rate Circular Gauge Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_rate_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Today's Completion Rate",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "${dailyProgress.completedHabitsToday} of ${dailyProgress.totalHabitsToday} habits completed",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = when {
                                dailyProgress.completionPercentage == 100 -> "Flawless execution today! 🌟"
                                dailyProgress.completionPercentage >= 70 -> "Strong momentum, keep going! 💪"
                                dailyProgress.completionPercentage > 0 -> "Off to a solid start today! 🚀"
                                else -> "Ready to check off your first habit!"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }

                    // Circular Progress Gauge
                    Box(
                        modifier = Modifier.size(96.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 10.dp.toPx()
                            // Background track
                            drawArc(
                                color = Color(0xFF1E293B),
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            // Progress arc
                            val sweep = (animatedPercent / 100f) * 360f
                            drawArc(
                                brush = Brush.sweepGradient(
                                    listOf(EmeraldPrimary, EmeraldLight, EmeraldPrimary)
                                ),
                                startAngle = -90f,
                                sweepAngle = sweep,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${dailyProgress.completionPercentage}%",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Done",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }

        // 2. Weekly Activity Bar Chart (Last 7 Days)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("weekly_trends_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = VioletSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Last 7 Days Activity",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Daily habit completion trends across the past week",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 7-day Bar Chart Canvas
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        weeklyTrends.forEach { dayTrend ->
                            val heightRatio = dayTrend.completionRatio.coerceIn(0.08f, 1f)

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                // Count badge
                                Text(
                                    text = "${dayTrend.totalCompleted}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 11.sp,
                                    color = if (dayTrend.totalCompleted > 0) EmeraldLight else TextMuted,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // The Bar
                                Box(
                                    modifier = Modifier
                                        .width(22.dp)
                                        .height(80.dp * heightRatio)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(
                                            if (dayTrend.totalCompleted > 0) {
                                                Brush.verticalGradient(
                                                    listOf(EmeraldLight, EmeraldPrimary)
                                                )
                                            } else {
                                                Brush.verticalGradient(
                                                    listOf(Color(0xFF334155), Color(0xFF1E293B))
                                                )
                                            }
                                        )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Day label
                                Text(
                                    text = dayTrend.dayLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Calendar View / Activity Heatmap (Last 28 Days)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("heatmap_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Consistency Heatmap",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Green dots represent days with completed habits",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // 4 rows x 7 columns grid
                    val chunks = heatmapCells.chunked(7)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        chunks.forEach { rowCells ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                rowCells.forEach { cell ->
                                    val cellBg = when {
                                        cell.completedCount == 0 -> Color(0xFF1E293B)
                                        cell.completionRatio >= 0.8f -> EmeraldPrimary
                                        cell.completionRatio >= 0.4f -> EmeraldPrimary.copy(alpha = 0.65f)
                                        else -> EmeraldPrimary.copy(alpha = 0.35f)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(cellBg)
                                            .border(
                                                width = if (cell.isToday) 2.dp else 0.dp,
                                                color = if (cell.isToday) Color.White else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${cell.dayOfMonth}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 11.sp,
                                            fontWeight = if (cell.completedCount > 0) FontWeight.Bold else FontWeight.Normal,
                                            color = if (cell.completedCount > 0) Color(0xFF022C22) else TextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Less",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        listOf(
                            Color(0xFF1E293B),
                            EmeraldPrimary.copy(alpha = 0.35f),
                            EmeraldPrimary.copy(alpha = 0.65f),
                            EmeraldPrimary
                        ).forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(color)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = "More",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        // 4. Category Breakdown Distribution
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("category_breakdown_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PieChart,
                            contentDescription = null,
                            tint = IndigoAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Category Distribution",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (categoryCompletions.isEmpty()) {
                        Text(
                            text = "Complete your first habits to see your category breakdown!",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    } else {
                        val categoryColors = listOf(
                            EmeraldPrimary,
                            VioletSecondary,
                            IndigoAccent,
                            Color(0xFFF59E0B),
                            Color(0xFFF43F5E),
                            Color(0xFF06B6D4)
                        )

                        categoryCompletions.forEachIndexed { index, (cat, count) ->
                            val color = categoryColors[index % categoryColors.size]
                            val percent = (count.toFloat() / totalCompletionsCount * 100).toInt()

                            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = cat,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = TextPrimary
                                        )
                                    }
                                    Text(
                                        text = "$count completions ($percent%)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(Color(0xFF1E293B))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(percent / 100f)
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(color)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
