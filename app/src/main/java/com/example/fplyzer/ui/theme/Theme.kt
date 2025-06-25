package com.example.fplyzer.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val FplLightColorScheme = lightColorScheme(
    primary = FplPrimary,
    onPrimary = FplTextOnPrimary,
    primaryContainer = FplPrimaryLight,
    onPrimaryContainer = FplTextOnPrimary,
    secondary = FplSecondary,
    onSecondary = FplTextOnPrimary,
    secondaryContainer = FplSecondaryLight,
    onSecondaryContainer = FplPrimary,
    tertiary = FplAccent,
    onTertiary = FplTextOnAccent,
    tertiaryContainer = FplAccentLight,
    onTertiaryContainer = FplPrimary,
    background = FplBackground,
    onBackground = FplTextPrimary,
    surface = FplSurface,
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
    primary = FplPrimaryLight,
    onPrimary = FplTextOnPrimary,
    primaryContainer = FplPrimary,
    onPrimaryContainer = FplAccent,
    secondary = FplSecondaryLight,
    onSecondary = FplPrimaryDark,
    secondaryContainer = FplSecondary,
    onSecondaryContainer = FplAccent,
    tertiary = FplAccent,
    onTertiary = FplPrimaryDark,
    tertiaryContainer = FplAccentDark,
    onTertiaryContainer = FplAccentLight,
    background = FplBackgroundDark,
    onBackground = FplTextOnPrimary,
    surface = FplPrimaryDark,
    onSurface = FplTextOnPrimary,
    surfaceVariant = FplPrimary,
    onSurfaceVariant = FplTextOnPrimary.copy(alpha = 0.8f),
    error = FplError,
    onError = Color.White,
    outline = FplGlassDark,
    outlineVariant = FplGlassDark.copy(alpha = 0.5f),
    scrim = FplOverlay
)

val ModernShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun FPLyzerTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) FplDarkColorScheme else FplLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = ModernShapes,
        content = content
    )
}