package com.georgehany.quickattend.ui.theme

import android.content.Context

object ThemePreferences {

    private const val PREFS_NAME = "quick_attend_preferences"
    private const val KEY_THEME_MODE = "theme_mode"

    fun getThemeMode(
        context: Context
    ): ThemeMode {
        val preferences = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        return when (
            preferences.getString(
                KEY_THEME_MODE,
                ThemeMode.LIGHT.name
            )
        ) {
            ThemeMode.DARK.name -> ThemeMode.DARK
            else -> ThemeMode.LIGHT
        }
    }

    fun saveThemeMode(
        context: Context,
        themeMode: ThemeMode
    ) {
        context
            .getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .putString(
                KEY_THEME_MODE,
                themeMode.name
            )
            .apply()
    }
}
