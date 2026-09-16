package com.georgehany.quickattend.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// ============================================================
// QuickAttend Material 3 Theme
// ============================================================

private val QuickAttendLightColorScheme = lightColorScheme(

    // Primary
    primary = NavyPrimary,
    onPrimary = NavyOnPrimary,

    primaryContainer = NavyContainer,
    onPrimaryContainer = NavyOnContainer,

    // Secondary / Accent
    secondary = AccentBlue,
    onSecondary = PureWhite,

    secondaryContainer = AccentBlueLight,
    onSecondaryContainer = AccentBlueDark,

    // Background
    background = NeutralBackground,
    onBackground = NeutralOnSurface,

    // Surfaces
    surface = NeutralSurface,
    onSurface = NeutralOnSurface,

    surfaceVariant = NeutralSurfaceSecondary,
    onSurfaceVariant = NeutralOnSurfaceSecondary,

    // Borders
    outline = NeutralOutline,
    outlineVariant = NeutralVariant,

    // Error
    error = RedNotPresent,
    onError = PureWhite,

    errorContainer = RedContainer,
    onErrorContainer = RedOnContainer,

    // Inverse colors
    inverseSurface = NeutralOnSurface,
    inverseOnSurface = PureWhite,
    inversePrimary = AccentBlueLight,

    // Scrim
    scrim = PureBlack
)

@Composable
fun QuickAttendTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = QuickAttendLightColorScheme,
        typography = Typography,
        content = content
    )
}
