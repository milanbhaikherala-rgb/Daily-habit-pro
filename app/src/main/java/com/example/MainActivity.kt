package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Habit
import com.example.ui.DailyHabitViewModel
import com.example.ui.components.BannerAdContainer
import com.example.ui.components.ConfettiCelebration
import com.example.ui.components.HabitDialog
import com.example.ui.screens.AchievementsScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.DailyHabitProTheme
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

enum class NavTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    TODAY("Today", Icons.Filled.CheckCircle, Icons.Outlined.CheckCircleOutline, "nav_tab_today"),
    ANALYTICS("Analytics", Icons.Filled.AutoGraph, Icons.Outlined.AutoGraph, "nav_tab_analytics"),
    ACHIEVEMENTS("Badges", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents, "nav_tab_achievements"),
    SETTINGS("Settings", Icons.Filled.Settings, Icons.Outlined.Settings, "nav_tab_settings")
}

class MainActivity : ComponentActivity() {

    private val viewModel: DailyHabitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DailyHabitProTheme(darkTheme = true) {
                DailyHabitProApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun DailyHabitProApp(viewModel: DailyHabitViewModel) {
    var currentTab by remember { mutableStateOf(NavTab.TODAY) }

    var isAddingHabit by remember { mutableStateOf(false) }
    var editingHabit by remember { mutableStateOf<Habit?>(null) }

    // State collection
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val dailyProgress by viewModel.dailyProgress.collectAsStateWithLifecycle()
    val currentQuote by viewModel.currentQuote.collectAsStateWithLifecycle()
    val habitItems by viewModel.habitItems.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()
    val weeklyTrends by viewModel.weeklyTrends.collectAsStateWithLifecycle()
    val heatmapCells by viewModel.heatmapCells.collectAsStateWithLifecycle()
    val badges by viewModel.badges.collectAsStateWithLifecycle()
    val allHabits by viewModel.allHabits.collectAsStateWithLifecycle()
    val allCompletions by viewModel.allCompletions.collectAsStateWithLifecycle()
    val confettiTrigger by viewModel.confettiTrigger.collectAsStateWithLifecycle()

    // Handle back button on secondary tabs
    if (currentTab != NavTab.TODAY) {
        BackHandler {
            currentTab = NavTab.TODAY
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg),
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Main Navigation Bar
                NavigationBar(
                    containerColor = DarkSurface,
                    tonalElevation = 0.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    NavTab.values().forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = tab },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title
                                )
                            },
                            label = { Text(text = tab.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF022C22),
                                selectedTextColor = EmeraldPrimary,
                                indicatorColor = EmeraldPrimary,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            ),
                            modifier = Modifier.testTag(tab.testTag)
                        )
                    }
                }

                // Dedicated Sticky 320x50 Banner Ad Container (fixed at bottom below nav bar)
                BannerAdContainer()
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                NavTab.TODAY -> {
                    HomeScreen(
                        selectedDate = selectedDate,
                        dailyProgress = dailyProgress,
                        currentQuote = currentQuote,
                        habitItems = habitItems,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { viewModel.setCategoryFilter(it) },
                        onToggleHabit = { viewModel.toggleHabit(it) },
                        onEditHabit = { editingHabit = it },
                        onDeleteHabit = { viewModel.deleteHabit(it) },
                        onAddHabitClick = { isAddingHabit = true },
                        onRefreshQuote = { viewModel.refreshQuote() }
                    )
                }

                NavTab.ANALYTICS -> {
                    AnalyticsScreen(
                        dailyProgress = dailyProgress,
                        weeklyTrends = weeklyTrends,
                        heatmapCells = heatmapCells,
                        allHabits = allHabits,
                        allCompletions = allCompletions
                    )
                }

                NavTab.ACHIEVEMENTS -> {
                    AchievementsScreen(
                        dailyProgress = dailyProgress,
                        badges = badges
                    )
                }

                NavTab.SETTINGS -> {
                    SettingsScreen(
                        totalHabitsCount = allHabits.size,
                        totalCompletionsCount = allCompletions.size,
                        onLoadStarters = { viewModel.preloadStarters() },
                        onClearAllData = { viewModel.clearAllData() }
                    )
                }
            }

            // Confetti Micro-animation Overlay
            ConfettiCelebration(
                triggerKey = confettiTrigger,
                onAnimationEnd = { viewModel.clearConfetti() }
            )
        }
    }

    // Add Habit Modal
    if (isAddingHabit) {
        HabitDialog(
            initialHabit = null,
            onDismiss = { isAddingHabit = false },
            onSave = { newHabit ->
                viewModel.saveHabit(newHabit)
                isAddingHabit = false
            }
        )
    }

    // Edit Habit Modal
    if (editingHabit != null) {
        HabitDialog(
            initialHabit = editingHabit,
            onDismiss = { editingHabit = null },
            onSave = { updatedHabit ->
                viewModel.saveHabit(updatedHabit)
                editingHabit = null
            }
        )
    }
}
