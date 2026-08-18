package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserSettings(
    val themeMode: String = "system", // "system", "light", "dark"
    val defaultPriority: TaskPriority = TaskPriority.MEDIUM,
    val defaultReminderMinutes: Int = 10,
    val firstDayOfWeek: Int = 1, // 1 = Monday, 7 = Sunday
    val timeFormat24h: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrateEnabled: Boolean = true,
    val userName: String = "Achiever"
)

@JsonClass(generateAdapter = true)
data class BackupData(
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val tasks: List<TaskItem> = emptyList(),
    val categories: List<TaskCategory> = emptyList(),
    val routines: List<RoutineItem> = emptyList(),
    val routineCompletions: List<RoutineCompletion> = emptyList()
)
