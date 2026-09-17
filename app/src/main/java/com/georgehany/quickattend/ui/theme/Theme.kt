package com.georgehany.quickattend.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val QuickAttendLightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,

    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,

    secondary = LightSecondary,
    onSecondary = LightOnSecondary,

    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,

    background = LightBackground,
    onBackground = LightOnBackground,

    surface = LightSurface,
    onSurface = LightOnSurface,

    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceSecondary,

    outline = LightOutline,
    outlineVariant = LightOutlineVariant,

    error = ErrorRed,
    onError = PureWhite,

    errorContainer = ErrorContainer,
    onErrorContainer = ErrorOnContainer,

    inverseSurface = LightOnSurface,
    inverseOnSurface = PureWhite,
    inversePrimary = LightPrimaryContainer,

    scrim = PureBlack
)

private val QuickAttendDarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,

    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,

    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,

    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,

    background = DarkBackground,
    onBackground = DarkOnBackground,

    surface = DarkSurface,
    onSurface = DarkOnSurface,

    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceSecondary,

    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,

    error = DarkErrorRed,
    onError = DarkErrorContainer,

    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkErrorOnContainer,

    inverseSurface = DarkOnSurface,
    inverseOnSurface = DarkBackground,
    inversePrimary = DarkPrimary,

    scrim = PureBlack
)

enum class ThemeMode {
    LIGHT,
    DARK
}

@Composable
fun QuickAttendTheme(
    themeMode: ThemeMode = ThemeMode.LIGHT,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        ThemeMode.LIGHT -> QuickAttendLightColorScheme
        ThemeMode.DARK -> QuickAttendDarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
