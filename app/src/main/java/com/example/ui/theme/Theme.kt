package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color(0xFF022C22),
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = EmeraldLight,
    
    secondary = VioletSecondary,
    onSecondary = Color(0xFF2E1065),
    secondaryContainer = Color(0xFF4C1D95),
    onSecondaryContainer = VioletLight,
    
    tertiary = IndigoAccent,
    onTertiary = Color(0xFF1E1B4B),
    tertiaryContainer = Color(0xFF312E81),
    onTertiaryContainer = IndigoLight,
    
    background = DarkBg,
    onBackground = TextPrimary,
    
    surface = DarkSurface,
    onSurface = TextPrimary,
    
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    
    outline = DarkBorder,
    outlineVariant = DarkBorderLight
)

private val LightColorScheme = darkColorScheme(
    // Default to dark palette to ensure consistent premium dark mode look as specified
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    secondary = VioletSecondary,
    onSecondary = Color.White,
    tertiary = IndigoAccent,
    background = DarkBg,
    surface = DarkSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder
)

@Composable
fun DailyHabitProTheme(
    darkTheme: Boolean = true, // Premium dark-mode requested as primary
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
