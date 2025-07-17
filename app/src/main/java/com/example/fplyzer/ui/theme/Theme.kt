package com.example.fplyzer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val FplLightColorScheme = lightColorScheme(
    primary = FplPrimary,
    onPrimary = Color.White,
    primaryContainer = FplPrimaryLight,
    onPrimaryContainer = Color.White,
    secondary = FplSecondary,
    onSecondary = Color.White,
    secondaryContainer = FplSecondaryLight,
    onSecondaryContainer = FplPrimary,
    tertiary = FplAccent,
    onTertiary = FplTextOnAccent,
    tertiaryContainer = FplAccentLight,
    onTertiaryContainer = FplPrimary,
    background = FplBackground,
    onBackground = FplTextPrimary,
    surface = Color.White,
    onSurface = FplTextPrimary,
    surfaceVariant = FplSurfaceVariant,
    onSurfaceVariant = FplTextSecondary,
    error = FplError,
    onError = Color.White,
    outline = FplDivider,
    outlineVariant = FplDivider.copy(alpha = 0.5f),
    scrim = FplOverlay
)

private val FplDarkColorScheme = darkColorScheme(
    primary = FplAccent,
    onPrimary = Color.Black,
    primaryContainer = FplPrimary,
    onPrimaryContainer = FplAccent,
    secondary = FplSecondaryLight,
    onSecondary = Color.Black,
    secondaryContainer = FplSecondary,
    onSecondaryContainer = FplAccent,
    tertiary = FplAccentLight,
    onTertiary = Color.Black,
    tertiaryContainer = FplAccentDark,
    onTertiaryContainer = FplAccentLight,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color.White.copy(alpha = 0.8f),
    error = FplError,
    onError = Color.White,
    outline = Color.White.copy(alpha = 0.2f),
    outlineVariant = Color.White.copy(alpha = 0.1f),
    scrim = FplOverlay
)

val ModernShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

// Composition Local for ThemeManager
val LocalThemeManager = compositionLocalOf<ThemeManager> {
    error("ThemeManager not provided")
}

@Composable
fun FPLyzerTheme(
    themeManager: ThemeManager? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val manager = themeManager ?: ThemeManagerHolder.getInstance(context)

    val isSystemInDarkTheme = isSystemInDarkTheme()

    LaunchedEffect(manager.currentMode, isSystemInDarkTheme) {
        manager.updateTheme(isSystemInDarkTheme)
    }

    val colorScheme = if (manager.isDarkMode) FplDarkColorScheme else FplLightColorScheme

    CompositionLocalProvider(LocalThemeManager provides manager) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = ModernShapes,
            content = content
        )
    }
}