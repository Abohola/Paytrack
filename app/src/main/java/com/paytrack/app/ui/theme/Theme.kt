package com.paytrack.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PaytrackColors = darkColorScheme(
    primary = Crimson,
    onPrimary = Color.White,
    primaryContainer = Burgundy,
    onPrimaryContainer = OffWhite,
    secondary = Scooter,
    onSecondary = DeepNoir,
    background = Noir,
    onBackground = OffWhite,
    surface = Color(0xFF11131A),
    onSurface = OffWhite,
    surfaceVariant = Color(0xFF20222A),
    onSurfaceVariant = Mist,
    outline = Color.White.copy(alpha = 0.18f),
    error = Color(0xFFFF6B78),
)

@Composable
fun PaytrackTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PaytrackColors,
        typography = PaytrackTypography,
        content = content,
    )
}
