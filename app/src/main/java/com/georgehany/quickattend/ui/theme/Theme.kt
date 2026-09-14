package com.georgehany.quickattend.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val QuickAttendLightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = NavyOnPrimary,
    primaryContainer = BlueContainer,
    onPrimaryContainer = BlueOnContainer,
    background = NeutralBackground,
    surface = NeutralSurface,
    onSurface = NeutralOnSurface,
    surfaceVariant = NeutralVariant,
    outline = NeutralOutline
)

@Composable
fun QuickAttendTheme(
    content: @Composable () -> Unit
) {
    // Strictly force Light Theme as requested
    MaterialTheme(
        colorScheme = QuickAttendLightColorScheme,
        typography = Typography,
        content = content
    )
}
