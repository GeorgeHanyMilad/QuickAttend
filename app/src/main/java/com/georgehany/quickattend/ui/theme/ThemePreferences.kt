package com.georgehany.quickattend.ui.theme

import android.content.Context

// ============================================================
// QuickAttend Theme Preferences
// ============================================================

private const val PREFS_NAME = "quickattend_preferences"
private const val THEME_MODE_KEY = "theme_mode"

object ThemePreferences {

    fun getThemeMode(context: Context): ThemeMode {
        val preferences = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        return when (
            preferences.getString(
                THEME_MODE_KEY,
                ThemeMode.SYSTEM.name
            )
        ) {
            ThemeMode.LIGHT.name -> ThemeMode.LIGHT
            ThemeMode.DARK.name -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    }

    fun saveThemeMode(
        context: Context,
        themeMode: ThemeMode
    ) {
        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
            .edit()
            .putString(
                THEME_MODE_KEY,
                themeMode.name
            )
            .apply()
    }
}
