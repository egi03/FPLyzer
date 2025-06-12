package com.example.fplyzer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FplColorScheme = lightColorScheme(
    primary = FplPrimary,
    onPrimary = FplTextOnPrimary,
    primaryContainer = FplPrimaryDark,
    onPrimaryContainer = FplTextOnPrimary,
    secondary = FplSecondary,
    onSecondary = FplTextOnPrimary,
    secondaryContainer = FplAccent,
    onSecondaryContainer = FplPrimary,
    tertiary = FplAccent,
    onTertiary = FplPrimary,
    background = FplBackground,
    onBackground = FplTextPrimary,
    surface = FplSurface,
    onSurface = FplTextPrimary,
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = FplTextSecondary,
    error = FplError,
    onError = Color.White
)

@Composable
fun FPLyzerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FplColorScheme,
        typography = Typography,
        content = content
    )
}