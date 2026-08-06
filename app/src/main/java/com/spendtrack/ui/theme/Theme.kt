package com.spendtrack.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8AB4FF),
    onPrimary = Color(0xFF08214C),
    primaryContainer = Color(0xFF234A88),
    onPrimaryContainer = Color(0xFFD7E7FF),
    secondary = Color(0xFF64D9B5),
    onSecondary = Color(0xFF003A2E),
    secondaryContainer = Color(0xFF005C49),
    onSecondaryContainer = Color(0xFFC9F4E8),
    tertiary = Color(0xFFFFB276),
    onTertiary = Color(0xFF4D2200),
    tertiaryContainer = Color(0xFF8A3D00),
    onTertiaryContainer = Color(0xFFFFE3CF),
    background = Color(0xFF0B1324),
    onBackground = Color(0xFFEAF0FA),
    surface = Color(0xFF111A2E),
    onSurface = Color(0xFFEAF0FA),
    surfaceVariant = Color(0xFF22304A),
    onSurfaceVariant = Color(0xFFCAD5E8),
    outline = Color(0xFF50627F)
)

@Composable
fun SpendTrackTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
