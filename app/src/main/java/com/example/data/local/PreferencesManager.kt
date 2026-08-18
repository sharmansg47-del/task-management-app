package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.TaskPriority
import com.example.data.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class PreferencesManager(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DEFAULT_PRIORITY = stringPreferencesKey("default_priority")
        val DEFAULT_REMINDER_MINUTES = intPreferencesKey("default_reminder_minutes")
        val FIRST_DAY_OF_WEEK = intPreferencesKey("first_day_of_week")
        val TIME_FORMAT_24H = booleanPreferencesKey("time_format_24h")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATE_ENABLED = booleanPreferencesKey("vibrate_enabled")
        val USER_NAME = stringPreferencesKey("user_name")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        val themeMode = preferences[PreferencesKeys.THEME_MODE] ?: "system"
        val defaultPriorityStr = preferences[PreferencesKeys.DEFAULT_PRIORITY] ?: TaskPriority.MEDIUM.name
        val defaultPriority = TaskPriority.fromString(defaultPriorityStr)
        val defaultReminder = preferences[PreferencesKeys.DEFAULT_REMINDER_MINUTES] ?: 10
        val firstDay = preferences[PreferencesKeys.FIRST_DAY_OF_WEEK] ?: 1
        val timeFormat24h = preferences[PreferencesKeys.TIME_FORMAT_24H] ?: false
        val notifications = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
        val sound = preferences[PreferencesKeys.SOUND_ENABLED] ?: true
        val vibrate = preferences[PreferencesKeys.VIBRATE_ENABLED] ?: true
        val userName = preferences[PreferencesKeys.USER_NAME] ?: "Achiever"

        UserSettings(
            themeMode = themeMode,
            defaultPriority = defaultPriority,
            defaultReminderMinutes = defaultReminder,
            firstDayOfWeek = firstDay,
            timeFormat24h = timeFormat24h,
            notificationsEnabled = notifications,
            soundEnabled = sound,
            vibrateEnabled = vibrate,
            userName = userName
        )
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[PreferencesKeys.THEME_MODE] = mode }
    }

    suspend fun setDefaultPriority(priority: TaskPriority) {
        context.dataStore.edit { it[PreferencesKeys.DEFAULT_PRIORITY] = priority.name }
    }

    suspend fun setDefaultReminderMinutes(minutes: Int) {
        context.dataStore.edit { it[PreferencesKeys.DEFAULT_REMINDER_MINUTES] = minutes }
    }

    suspend fun setFirstDayOfWeek(day: Int) {
        context.dataStore.edit { it[PreferencesKeys.FIRST_DAY_OF_WEEK] = day }
    }

    suspend fun setTimeFormat24h(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.TIME_FORMAT_24H] = enabled }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SOUND_ENABLED] = enabled }
    }

    suspend fun setVibrateEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.VIBRATE_ENABLED] = enabled }
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { it[PreferencesKeys.USER_NAME] = name }
    }
}
