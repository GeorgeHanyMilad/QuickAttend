```kotlin
package com.georgehany.quickattend.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// ============================================================
// QuickAttend Material 3 Theme
// ============================================================

private val QuickAttendLightColorScheme = lightColorScheme(

    // Primary
    primary = LightPrimary,
    onPrimary = LightOnPrimary,

    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,

    // Secondary
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,

    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,

    // Background
    background = LightBackground,
    onBackground = LightOnBackground,

    // Surface
    surface = LightSurface,
    onSurface = LightOnSurface,

    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceSecondary,

    // Outline
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,

    // Error
    error = ErrorRed,
    onError = PureWhite,

    errorContainer = ErrorContainer,
    onErrorContainer = ErrorOnContainer,

    // Inverse
    inverseSurface = LightOnSurface,
    inverseOnSurface = PureWhite,
    inversePrimary = LightPrimaryContainer,

    // Scrim
    scrim = PureBlack
)

// ============================================================
// DARK COLOR SCHEME
// ============================================================

private val QuickAttendDarkColorScheme = darkColorScheme(

    // Primary
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,

    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,

    // Secondary
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,

    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,

    // Background
    background = DarkBackground,
    onBackground = DarkOnBackground,

    // Surface
    surface = DarkSurface,
    onSurface = DarkOnSurface,

    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceSecondary,

    // Outline
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,

    // Error
    error = DarkErrorRed,
    onError = DarkErrorContainer,

    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkErrorOnContainer,

    // Inverse
    inverseSurface = DarkOnSurface,
    inverseOnSurface = DarkBackground,
    inversePrimary = DarkPrimary,

    // Scrim
    scrim = PureBlack
)

// ============================================================
// THEME MODE
// ============================================================

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

// ============================================================
// QUICKATTEND THEME
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
```
