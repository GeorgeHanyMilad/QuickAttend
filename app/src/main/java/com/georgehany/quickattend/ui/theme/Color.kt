package com.georgehany.quickattend.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================================
// QuickAttend — Professional Academic Blue Palette
// ============================================================

// ---------- Brand ----------
val QuickAttendBlue = Color(0xFF2563EB)
val QuickAttendBlueDark = Color(0xFF1D4ED8)
val QuickAttendBlueLight = Color(0xFF60A5FA)

val QuickAttendNavy = Color(0xFF173B73)
val QuickAttendNavyDark = Color(0xFF102A52)

// ---------- Light Theme ----------
val LightBackground = Color(0xFFF5F8FC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF0F4F9)
val LightSurfaceElevated = Color(0xFFFFFFFF)

val LightOnBackground = Color(0xFF101828)
val LightOnSurface = Color(0xFF172033)
val LightOnSurfaceSecondary = Color(0xFF667085)
val LightOnSurfaceTertiary = Color(0xFF98A2B3)

val LightOutline = Color(0xFFD0D5DD)
val LightOutlineVariant = Color(0xFFE4E7EC)

// ---------- Dark Theme ----------
val DarkBackground = Color(0xFF0A1220)
val DarkSurface = Color(0xFF111C2D)
val DarkSurfaceVariant = Color(0xFF18263A)
val DarkSurfaceElevated = Color(0xFF1D2D44)

val DarkOnBackground = Color(0xFFF5F8FC)
val DarkOnSurface = Color(0xFFEAF0F8)
val DarkOnSurfaceSecondary = Color(0xFFAAB8CC)
val DarkOnSurfaceTertiary = Color(0xFF7F8EA3)

val DarkOutline = Color(0xFF3A4A60)
val DarkOutlineVariant = Color(0xFF29384D)

// ============================================================
// Primary
// ============================================================

val LightPrimary = Color(0xFF1D4ED8)
val LightOnPrimary = Color(0xFFFFFFFF)

val LightPrimaryContainer = Color(0xFFDBEAFE)
val LightOnPrimaryContainer = Color(0xFF173B73)

val DarkPrimary = Color(0xFF8DB8FF)
val DarkOnPrimary = Color(0xFF092B63)

val DarkPrimaryContainer = Color(0xFF173F78)
val DarkOnPrimaryContainer = Color(0xFFD9E8FF)

// ============================================================
// Secondary
// ============================================================

val LightSecondary = Color(0xFF2563EB)
val LightOnSecondary = Color(0xFFFFFFFF)

val LightSecondaryContainer = Color(0xFFE5EEFF)
val LightOnSecondaryContainer = Color(0xFF173B73)

val DarkSecondary = Color(0xFF8FB7FF)
val DarkOnSecondary = Color(0xFF0A2A5C)

val DarkSecondaryContainer = Color(0xFF1D3E73)
val DarkOnSecondaryContainer = Color(0xFFDCE9FF)

// ============================================================
// Status Colors
// ============================================================

val Success = Color(0xFF159A68)
val SuccessDark = Color(0xFF087F50)
val SuccessContainer = Color(0xFFE8F7F0)
val SuccessOnContainer = Color(0xFF087F50)

val DarkSuccess = Color(0xFF55D6A0)
val DarkSuccessContainer = Color(0xFF164B39)
val DarkSuccessOnContainer = Color(0xFFA5F2D0)

val ErrorRed = Color(0xFFD64545)
val ErrorRedDark = Color(0xFFB83232)
val ErrorContainer = Color(0xFFFDECEC)
val ErrorOnContainer = Color(0xFFB83232)

val DarkErrorRed = Color(0xFFFF8A82)
val DarkErrorContainer = Color(0xFF5B2425)
val DarkErrorOnContainer = Color(0xFFFFDAD7)

val Warning = Color(0xFFD98A16)
val WarningContainer = Color(0xFFFFF4DE)
val WarningOnContainer = Color(0xFF8A5200)

val DarkWarning = Color(0xFFFFC15A)
val DarkWarningContainer = Color(0xFF523A12)
val DarkWarningOnContainer = Color(0xFFFFDFA3)

// ============================================================
// Information
// ============================================================

val Info = Color(0xFF2563EB)
val InfoContainer = Color(0xFFE8F1FF)
val InfoOnContainer = Color(0xFF1D4ED8)

val DarkInfo = Color(0xFF8DB8FF)
val DarkInfoContainer = Color(0xFF1C3C70)
val DarkInfoOnContainer = Color(0xFFD9E8FF)

// ============================================================
// Neutral / Utility
// ============================================================

val PureWhite = Color(0xFFFFFFFF)
val PureBlack = Color(0xFF000000)
val Transparent = Color(0x00000000)

// ============================================================
// Backward-compatible aliases
// These keep existing screens compiling while we redesign them.
// ============================================================

val NavyPrimary = QuickAttendNavy
val NavyPrimaryDark = QuickAttendNavyDark
val NavyPrimaryLight = QuickAttendBlueLight
val NavyOnPrimary = PureWhite
val NavyContainer = LightPrimaryContainer
val NavyOnContainer = LightOnPrimaryContainer

val AccentBlue = QuickAttendBlue
val AccentBlueLight = LightPrimaryContainer
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
