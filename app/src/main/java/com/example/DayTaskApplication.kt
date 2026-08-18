package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.local.PreferencesManager
import com.example.data.repository.CategoryRepository
import com.example.data.repository.RoutineRepository
import com.example.data.repository.TaskRepository
import com.example.notification.AlarmScheduler
import com.example.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class DayTaskApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val taskRepository by lazy { TaskRepository(database.taskDao()) }
    val categoryRepository by lazy { CategoryRepository(database.categoryDao()) }
    val routineRepository by lazy { RoutineRepository(database.routineDao()) }
    val preferencesManager by lazy { PreferencesManager(this) }
    val alarmScheduler by lazy { AlarmScheduler(this) }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannels(this)
    }
}
