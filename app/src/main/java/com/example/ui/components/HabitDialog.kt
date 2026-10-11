package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Habit
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderLight
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HabitDialog(
    initialHabit: Habit? = null,
    onDismiss: () -> Unit,
    onSave: (Habit) -> Unit
) {
    var name by remember { mutableStateOf(initialHabit?.name ?: "") }
    var category by remember { mutableStateOf(initialHabit?.category ?: "Health") }
    var customCategoryText by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf(initialHabit?.icon ?: "💧") }
    var selectedColorHex by remember { mutableStateOf(initialHabit?.colorHex ?: 0xFF10B981) }
    var frequencyType by remember { mutableStateOf(initialHabit?.frequencyType ?: "DAILY") }
    
    // Days of week: 1=Mon .. 7=Sun
    val initialDays = remember(initialHabit) {
        initialHabit?.targetDaysOfWeek?.split(",")?.mapNotNull { it.trim().toIntOrNull() }
            ?: listOf(1, 2, 3, 4, 5, 6, 7)
    }
    val selectedDays = remember { mutableStateListOf<Int>().apply { addAll(initialDays) } }

    var targetValueString by remember { mutableStateOf(initialHabit?.targetValue?.toString() ?: "1") }
    var targetUnit by remember { mutableStateOf(initialHabit?.targetUnit ?: "times") }

    var isError by remember { mutableStateOf(false) }

    val presetCategories = listOf("Health", "Study", "Fitness", "Work", "Mind", "Custom")
    val presetIcons = listOf("💧", "🏃", "📚", "🧘", "🥗", "💊", "💤", "💻", "🎯", "✍️", "🍎", "🎨", "🚴", "🚶")
    val presetColors = listOf(
        0xFF10B981, // Emerald
        0xFF8B5CF6, // Violet
        0xFF6366F1, // Indigo
        0xFFF59E0B, // Amber
        0xFFF43F5E, // Rose
        0xFF06B6D4  // Cyan
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("habit_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialHabit == null) "Create New Habit" else "Edit Habit",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("habit_dialog_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Habit Name Input
                Text(
                    text = "HABIT NAME",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) isError = false
                    },
                    placeholder = { Text("e.g. Drink 2L Water, Read 15 mins", color = TextMuted) },
                    singleLine = true,
                    isError = isError,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = DarkBorder,
                        errorBorderColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("habit_name_input")
                )
                if (isError) {
                    Text(
                        text = "Habit name cannot be empty",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Icon Picker
                Text(
                    text = "ICON / EMOJI",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetIcons.forEach { iconEmoji ->
                        val isSelected = selectedIcon == iconEmoji
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) EmeraldPrimary.copy(alpha = 0.25f) else Color(0xFF1E293B))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) EmeraldPrimary else DarkBorder,
                                    shape = CircleShape
                                )
                                .clickable { selectedIcon = iconEmoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = iconEmoji, fontSize = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category Selector
                Text(
                    text = "CATEGORY",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetCategories.forEach { cat ->
                        val isSelected = category == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) EmeraldPrimary else Color(0xFF1E293B))
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) EmeraldPrimary else DarkBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { category = cat }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                                .testTag("category_option_$cat")
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) Color(0xFF022C22) else TextPrimary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                if (category == "Custom") {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customCategoryText,
                        onValueChange = { customCategoryText = it },
                        placeholder = { Text("Enter custom category", color = TextMuted) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = DarkBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_category_input")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Color Accent
                Text(
                    text = "ACCENT COLOR",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    presetColors.forEach { colorVal ->
                        val isSelected = selectedColorHex == colorVal
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(colorVal))
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) TextPrimary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColorHex = colorVal }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Frequency Selector
                Text(
                    text = "FREQUENCY",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("DAILY" to "Every Day", "SPECIFIC_DAYS" to "Specific Days").forEach { (type, label) ->
                        val isSelected = frequencyType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFF1E293B))
                                .border(
                                    width = 1.5.dp,
                                    color = if (isSelected) EmeraldPrimary else DarkBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { frequencyType = type }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) EmeraldPrimary else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // If specific days: show day-of-week toggles
                if (frequencyType == "SPECIFIC_DAYS") {
                    Spacer(modifier = Modifier.height(10.dp))
                    val daysLabels = listOf(
                        1 to "M", 2 to "T", 3 to "W", 4 to "T", 5 to "F", 6 to "S", 7 to "S"
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        daysLabels.forEach { (dayNum, label) ->
                            val isDaySelected = selectedDays.contains(dayNum)
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isDaySelected) EmeraldPrimary else Color(0xFF1E293B))
                                    .border(
                                        width = 1.dp,
                                        color = if (isDaySelected) EmeraldPrimary else DarkBorder,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        if (isDaySelected) {
                                            if (selectedDays.size > 1) selectedDays.remove(dayNum)
                                        } else {
                                            selectedDays.add(dayNum)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isDaySelected) Color(0xFF022C22) else TextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Goal Target (Value + Unit)
                Text(
                    text = "GOAL TARGET",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = targetValueString,
                        onValueChange = { targetValueString = it.filter { char -> char.isDigit() } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Target", color = TextMuted) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = DarkBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("target_value_input")
                    )

                    OutlinedTextField(
                        value = targetUnit,
                        onValueChange = { targetUnit = it },
                        label = { Text("Unit (e.g. times, mins)", color = TextMuted) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = DarkBorder
                        ),
                        modifier = Modifier
                            .weight(1.4f)
                            .testTag("target_unit_input")
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("cancel_habit_button"),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)
                        )
                    ) {
                        Text("Cancel", color = TextSecondary)
                    }

                    Button(
                        onClick = {
                            val trimmedName = name.trim()
                            if (trimmedName.isBlank()) {
                                isError = true
                                return@Button
                            }
                            val finalCategory = if (category == "Custom") {
                                customCategoryText.trim().ifBlank { "Custom" }
                            } else {
                                category
                            }
                            val finalValue = targetValueString.toIntOrNull() ?: 1
                            val sortedDays = selectedDays.sorted().joinToString(",")

                            val updatedHabit = Habit(
                                id = initialHabit?.id ?: 0L,
                                name = trimmedName,
                                category = finalCategory,
                                icon = selectedIcon,
                                colorHex = selectedColorHex,
                                frequencyType = frequencyType,
                                targetDaysOfWeek = if (frequencyType == "DAILY") "1,2,3,4,5,6,7" else sortedDays,
                                targetValue = if (finalValue > 0) finalValue else 1,
                                targetUnit = targetUnit.trim().ifBlank { "times" },
                                createdAt = initialHabit?.createdAt ?: System.currentTimeMillis()
                            )
                            onSave(updatedHabit)
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = Color(0xFF022C22)
                        ),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp)
                            .testTag("save_habit_button")
                    ) {
                        Text(
                            text = if (initialHabit == null) "Create Habit" else "Save Changes",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
