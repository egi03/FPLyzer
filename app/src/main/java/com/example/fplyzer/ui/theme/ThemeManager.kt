package com.example.fplyzer.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppearanceMode(val displayName: String, val icon: String) {
    SYSTEM("System", "settings"),
    LIGHT("Light", "light_mode"),
    DARK("Dark", "dark_mode")
}

class ThemeManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    private val themeKey = "app_theme_preference"

    var currentMode by mutableStateOf(loadThemePreference())
        private set

    var isDarkMode by mutableStateOf(false)
        private set

    private fun loadThemePreference(): AppearanceMode {
        val savedTheme = prefs.getString(themeKey, AppearanceMode.SYSTEM.name) ?: AppearanceMode.SYSTEM.name
        return try {
            AppearanceMode.valueOf(savedTheme)
        } catch (e: IllegalArgumentException) {
            AppearanceMode.SYSTEM
        }
    }

    private fun saveThemePreference() {
        prefs.edit().putString(themeKey, currentMode.name).apply()
    }

    fun setTheme(mode: AppearanceMode) {
        println("DEBUG: ThemeManager.setTheme called with: ${mode.displayName}")
        println("DEBUG: Previous mode was: ${currentMode.displayName}")
        currentMode = mode
        saveThemePreference()
        println("DEBUG: New mode set to: ${currentMode.displayName}")
    }

    fun updateTheme(isSystemInDarkTheme: Boolean) {
        val previousDarkMode = isDarkMode
        isDarkMode = when (currentMode) {
            AppearanceMode.SYSTEM -> isSystemInDarkTheme
            AppearanceMode.LIGHT -> false
            AppearanceMode.DARK -> true
        }
        println("DEBUG: updateTheme - mode: ${currentMode.displayName}, isSystemDark: $isSystemInDarkTheme, isDarkMode: $isDarkMode (was: $previousDarkMode)")
    }

    fun toggleTheme() {
        val newMode = when (currentMode) {
            AppearanceMode.SYSTEM, AppearanceMode.LIGHT -> AppearanceMode.DARK
            AppearanceMode.DARK -> AppearanceMode.LIGHT
        }
        setTheme(newMode)
    }
}

// Singleton holder for the ThemeManager
object ThemeManagerHolder {
    private var _instance: ThemeManager? = null

    fun getInstance(context: Context): ThemeManager {
        return _instance ?: ThemeManager(context.applicationContext).also { _instance = it }
    }
}