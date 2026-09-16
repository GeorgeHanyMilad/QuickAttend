```kotlin
package com.georgehany.quickattend.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================================
// QuickAttend Design System — Color.kt
// ============================================================

// ============================================================
// BRAND COLORS
// ============================================================

val QuickAttendBlue = Color(0xFF2563EB)
val QuickAttendBlueDark = Color(0xFF1D4ED8)
val QuickAttendBlueLight = Color(0xFF60A5FA)

val QuickAttendNavy = Color(0xFF173B73)
val QuickAttendNavyDark = Color(0xFF102A52)

val QuickAttendSky = Color(0xFFEFF6FF)

// ============================================================
// LIGHT MODE
// ============================================================

val LightBackground = Color(0xFFF6F8FC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F4F9)
val LightSurfaceElevated = Color(0xFFFFFFFF)

val LightOnBackground = Color(0xFF111827)
val LightOnSurface = Color(0xFF172033)
val LightOnSurfaceSecondary = Color(0xFF64748B)
val LightOnSurfaceTertiary = Color(0xFF94A3B8)

val LightOutline = Color(0xFFD7DEE9)
val LightOutlineVariant = Color(0xFFE8EDF4)

// ============================================================
// DARK MODE
// ============================================================

val DarkBackground = Color(0xFF0B1220)
val DarkSurface = Color(0xFF111A2A)
val DarkSurfaceVariant = Color(0xFF182235)
val DarkSurfaceElevated = Color(0xFF1E2A3D)

val DarkOnBackground = Color(0xFFF1F5F9)
val DarkOnSurface = Color(0xFFE8EEF7)
val DarkOnSurfaceSecondary = Color(0xFFAAB7C9)
val DarkOnSurfaceTertiary = Color(0xFF7F8DA3)

val DarkOutline = Color(0xFF39475A)
val DarkOutlineVariant = Color(0xFF293548)

// ============================================================
// PRIMARY / ACCENT
// ============================================================

val LightPrimary = Color(0xFF1D4ED8)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFDBEAFE)
val LightOnPrimaryContainer = Color(0xFF173B73)

val DarkPrimary = Color(0xFF8DB8FF)
val DarkOnPrimary = Color(0xFF092B63)
val DarkPrimaryContainer = Color(0xFF173F78)
val DarkOnPrimaryContainer = Color(0xFFD9E8FF)

// Secondary Accent

val LightSecondary = Color(0xFF2563EB)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFE0ECFF)
val LightOnSecondaryContainer = Color(0xFF173B73)

val DarkSecondary = Color(0xFF8FB7FF)
val DarkOnSecondary = Color(0xFF0A2A5C)
val DarkSecondaryContainer = Color(0xFF1D3E73)
val DarkOnSecondaryContainer = Color(0xFFDCE9FF)

// ============================================================
// SUCCESS / PRESENT
// ============================================================

val Success = Color(0xFF16A36A)
val SuccessDark = Color(0xFF087F50)

val SuccessContainer = Color(0xFFE7F7EF)
val SuccessOnContainer = Color(0xFF087F50)

val DarkSuccess = Color(0xFF55D6A0)
val DarkSuccessContainer = Color(0xFF164B39)
val DarkSuccessOnContainer = Color(0xFFA5F2D0)

// ============================================================
// ERROR / ABSENT
// ============================================================

val ErrorRed = Color(0xFFE05252)
val ErrorRedDark = Color(0xFFBE3434)

val ErrorContainer = Color(0xFFFDECEC)
val ErrorOnContainer = Color(0xFFB83232)

val DarkErrorRed = Color(0xFFFF8A82)
val DarkErrorContainer = Color(0xFF5B2425)
val DarkErrorOnContainer = Color(0xFFFFDAD7)

// ============================================================
// WARNING / IN PROGRESS
// ============================================================

val Warning = Color(0xFFE89B2C)
val WarningContainer = Color(0xFFFFF4DE)
val WarningOnContainer = Color(0xFF9A5B00)

val DarkWarning = Color(0xFFFFC15A)
val DarkWarningContainer = Color(0xFF523A12)
val DarkWarningOnContainer = Color(0xFFFFDFA3)

// ============================================================
// INFO
// ============================================================

val Info = Color(0xFF2563EB)
val InfoContainer = Color(0xFFE8F1FF)
val InfoOnContainer = Color(0xFF1D4ED8)

val DarkInfo = Color(0xFF8DB8FF)
val DarkInfoContainer = Color(0xFF1C3C70)
val DarkInfoOnContainer = Color(0xFFD9E8FF)

// ============================================================
// PURE / UTILITY COLORS
// ============================================================

val PureWhite = Color(0xFFFFFFFF)
val PureBlack = Color(0xFF000000)
val Transparent = Color(0x00000000)

// ============================================================
// LEGACY ALIASES
// ------------------------------------------------------------
// Kept so existing files can continue compiling while we
// redesign the screens one by one.
// ============================================================

val NavyPrimary = QuickAttendNavy
val NavyPrimaryDark = QuickAttendNavyDark
val NavyPrimaryLight = QuickAttendBlueLight

val NavyOnPrimary = PureWhite
val NavyContainer = LightPrimaryContainer
val NavyOnContainer = LightOnPrimaryContainer

val AccentBlue = QuickAttendBlue
val AccentBlueLight = Color(0xFFDBEAFE)
val AccentBlueDark = QuickAttendBlueDark

val EmeraldPresent = Success
val EmeraldPresentDark = SuccessDark
val EmeraldContainer = SuccessContainer
val EmeraldOnContainer = SuccessOnContainer

val RedNotPresent = ErrorRed
val RedNotPresentDark = ErrorRedDark
val RedContainer = ErrorContainer
val RedOnContainer = ErrorOnContainer

val AmberWarning = Warning
val AmberWarningContainer = WarningContainer
val AmberOnWarningContainer = WarningOnContainer

val NeutralBackground = LightBackground
val NeutralSurface = LightSurface
val NeutralSurfaceSecondary = LightSurfaceVariant

val NeutralOnSurface = LightOnSurface
val NeutralOnSurfaceSecondary = LightOnSurfaceSecondary
val NeutralOnSurfaceTertiary = LightOnSurfaceTertiary

val NeutralVariant = LightOutlineVariant
val NeutralOutline = LightOutline
val NeutralOutlineLight = LightOutlineVariant

val DarkNavyPrimary = DarkPrimary
val DarkNavyPrimaryContainer = DarkPrimaryContainer
val DarkNavyOnPrimary = DarkOnPrimary
val DarkNavyOnContainer = DarkOnPrimaryContainer

val DarkAccentBlue = DarkSecondary
val DarkAccentBlueContainer = DarkSecondaryContainer
val DarkAccentBlueOnContainer = DarkOnSecondaryContainer

val DarkEmeraldPresent = DarkSuccess
val DarkEmeraldContainer = DarkSuccessContainer
val DarkEmeraldOnContainer = DarkSuccessOnContainer

val DarkRedNotPresent = DarkErrorRed
val DarkRedContainer = DarkErrorContainer
val DarkRedOnContainer = DarkErrorOnContainer

val DarkAmberWarning = DarkWarning
val DarkAmberContainer = DarkWarningContainer
val DarkAmberOnContainer = DarkWarningOnContainer

val DarkSurfaceSecondary = DarkSurfaceVariant

val DarkNeutralVariant = DarkOutlineVariant
val DarkNeutralOutline = DarkOutline
val DarkNeutralOutlineLight = DarkOutlineVariant

val SurfaceOverlay = Color(0x14000000)
```
