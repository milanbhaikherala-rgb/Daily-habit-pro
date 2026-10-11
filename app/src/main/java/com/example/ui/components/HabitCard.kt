package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HabitItemUiState
import com.example.ui.theme.AmberStreak
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderLight
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HabitCard(
    habitState: HabitItemUiState,
    onToggleCompletion: () -> Unit,
    onEditHabit: () -> Unit,
    onDeleteHabit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val habit = habitState.habit
    val isCompleted = habitState.isCompletedToday
    var showMenu by remember { mutableStateOf(false) }

    val accentColor = remember(habit.colorHex) { Color(habit.colorHex) }

    val cardBg by animateColorAsState(
        targetValue = if (isCompleted) Color(0xFF0F2620) else DarkSurfaceCard,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "cardBg"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isCompleted) EmeraldPrimary.copy(alpha = 0.5f) else DarkBorder,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "borderColor"
    )

    val checkScale by animateFloatAsState(
        targetValue = if (isCompleted) 1.05f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "checkScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardBg)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(20.dp))
            .padding(14.dp)
            .testTag("habit_card_${habit.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox Button with micro-animation
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .scale(checkScale)
                    .clip(CircleShape)
                    .background(
                        if (isCompleted) EmeraldPrimary else Color(0xFF1E293B)
                    )
                    .border(
                        width = 2.dp,
                        color = if (isCompleted) EmeraldPrimary else DarkBorderLight,
                        shape = CircleShape
                    )
                    .clickable { onToggleCompletion() }
                    .testTag("habit_toggle_${habit.id}"),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color(0xFF022C22),
                        modifier = Modifier.size(26.dp)
                    )
                } else {
                    // Show habit emoji/icon inside circle before check
                    Text(
                        text = habit.icon.ifBlank { "✨" },
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Habit Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = habit.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isCompleted) TextSecondary else TextPrimary,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Category pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(accentColor.copy(alpha = 0.15f))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = habit.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = accentColor,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Target quantity pill if applicable
                    if (habit.targetValue > 1 || habit.targetUnit != "times") {
                        Text(
                            text = "🎯 ${habit.targetValue} ${habit.targetUnit}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Streak Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (habitState.currentStreak > 0) AmberStreak.copy(alpha = 0.15f)
                        else Color(0xFF1E293B).copy(alpha = 0.6f)
                    )
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = "Streak",
                    tint = if (habitState.currentStreak > 0) AmberStreak else TextMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "${habitState.currentStreak}d",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (habitState.currentStreak > 0) AmberStreak else TextMuted
                )
            }

            // More Options (Edit / Delete)
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("habit_options_${habit.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(DarkSurfaceCard)
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit Habit", color = TextPrimary) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = TextSecondary
                            )
                        },
                        onClick = {
                            showMenu = false
                            onEditHabit()
                        },
                        modifier = Modifier.testTag("edit_habit_${habit.id}")
                    )
                    DropdownMenuItem(
                        text = { Text("Delete Habit", color = ErrorRed) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                tint = ErrorRed
                            )
                        },
                        onClick = {
                            showMenu = false
                            onDeleteHabit()
                        },
                        modifier = Modifier.testTag("delete_habit_${habit.id}")
                    )
                }
            }
        }
    }
}
