package com.georgehany.quickattend.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// ============================================================
// QuickAttend Material 3 Theme
// ============================================================

// ------------------------------------------------------------
// Light Color Scheme
// ------------------------------------------------------------

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

// ------------------------------------------------------------
// Dark Color Scheme
// ------------------------------------------------------------

private val QuickAttendDarkColorScheme = darkColorScheme(

    // Primary
    primary = DarkNavyPrimary,
    onPrimary = DarkNavyOnPrimary,

    primaryContainer = DarkNavyPrimaryContainer,
    onPrimaryContainer = DarkNavyOnContainer,

    // Secondary / Accent
    secondary = DarkAccentBlue,
    onSecondary = DarkNavyOnPrimary,

    secondaryContainer = DarkAccentBlueContainer,
    onSecondaryContainer = DarkAccentBlueOnContainer,

    // Background
    background = DarkBackground,
    onBackground = DarkOnSurface,

    // Surfaces
    surface = DarkSurface,
    onSurface = DarkOnSurface,

    surfaceVariant = DarkSurfaceSecondary,
    onSurfaceVariant = DarkOnSurfaceSecondary,

    // Borders
    outline = DarkNeutralOutline,
    outlineVariant = DarkNeutralVariant,

    // Error
    error = DarkRedNotPresent,
    onError = DarkRedOnContainer,

    errorContainer = DarkRedContainer,
    onErrorContainer = DarkRedOnContainer,

    // Inverse colors
    inverseSurface = DarkOnSurface,
    inverseOnSurface = DarkBackground,
    inversePrimary = DarkNavyPrimary,

    // Scrim
    scrim = PureBlack
)

// ============================================================
// Theme Mode
// ============================================================

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

// ============================================================
// QuickAttend Theme
// ============================================================

@Composable
fun QuickAttendTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    MaterialTheme(
        colorScheme = if (darkTheme) {
            QuickAttendDarkColorScheme
        } else {
            QuickAttendLightColorScheme
        },
        typography = Typography,
        content = content
    )
}
