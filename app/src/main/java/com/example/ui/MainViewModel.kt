package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.DayTaskApplication
import com.example.data.local.AppDatabase
import com.example.data.local.PreferencesManager
import com.example.data.model.BackupData
import com.example.data.model.RepeatFrequency
import com.example.data.model.RoutineCompletion
import com.example.data.model.RoutineItem
import com.example.data.model.TaskCategory
import com.example.data.model.TaskItem
import com.example.data.model.TaskPriority
import com.example.data.model.UserSettings
import com.example.data.repository.CategoryRepository
import com.example.data.repository.RoutineRepository
import com.example.data.repository.TaskRepository
import com.example.notification.AlarmScheduler
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class MainViewModel(
    application: Application,
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val routineRepository: RoutineRepository,
    private val preferencesManager: PreferencesManager,
    private val alarmScheduler: AlarmScheduler
) : AndroidViewModel(application) {

    val tasks: StateFlow<List<TaskItem>> = taskRepository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<TaskCategory>> = categoryRepository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val routines: StateFlow<List<RoutineItem>> = routineRepository.allRoutines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val routineCompletions: StateFlow<List<RoutineCompletion>> = routineRepository.allCompletions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userSettings: StateFlow<UserSettings> = preferencesManager.userSettingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    // --- Task Actions ---

    fun insertTask(task: TaskItem) {
        viewModelScope.launch {
            val newId = taskRepository.insertTask(task)
            val insertedTask = task.copy(id = newId)
            if (userSettings.value.notificationsEnabled) {
                alarmScheduler.scheduleTaskReminder(insertedTask)
            }
        }
    }

    fun updateTask(task: TaskItem) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
            if (userSettings.value.notificationsEnabled && !task.isCompleted) {
                alarmScheduler.scheduleTaskReminder(task)
            } else {
                alarmScheduler.cancelTaskReminder(task.id)
            }
        }
    }

    fun deleteTask(task: TaskItem) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
            alarmScheduler.cancelTaskReminder(task.id)
        }
    }

    fun toggleTaskComplete(task: TaskItem) {
        viewModelScope.launch {
            val newCompleted = !task.isCompleted
            taskRepository.setTaskCompleted(task, newCompleted)
            if (newCompleted) {
                alarmScheduler.cancelTaskReminder(task.id)
            } else if (userSettings.value.notificationsEnabled) {
                alarmScheduler.scheduleTaskReminder(task.copy(isCompleted = false))
            }
        }
    }

    fun duplicateTask(task: TaskItem) {
        viewModelScope.launch {
            val duplicated = task.copy(
                id = 0,
                title = "${task.title} (Copy)",
                isCompleted = false,
                completedAtMillis = null,
                createdAtMillis = System.currentTimeMillis()
            )
            val newId = taskRepository.insertTask(duplicated)
            if (userSettings.value.notificationsEnabled) {
                alarmScheduler.scheduleTaskReminder(duplicated.copy(id = newId))
            }
        }
    }

    fun clearCompletedTasks() {
        viewModelScope.launch {
            val all = taskRepository.getAllTasksDirect()
            all.filter { it.isCompleted }.forEach { completed ->
                taskRepository.deleteTask(completed)
            }
        }
    }

    // --- Category Actions ---

    fun addCategory(name: String, iconName: String, colorHex: String) {
        viewModelScope.launch {
            val cat = TaskCategory(
                name = name,
                iconName = iconName,
                colorHex = colorHex,
                isDefault = false
            )
            categoryRepository.insertCategory(cat)
        }
    }

    fun updateCategory(category: TaskCategory) {
        viewModelScope.launch {
            categoryRepository.updateCategory(category)
        }
    }

    fun deleteCategory(category: TaskCategory) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(category)
        }
    }

    // --- Routine Actions ---

    fun addRoutine(routine: RoutineItem) {
        viewModelScope.launch {
            routineRepository.insertRoutine(routine)
        }
    }

    fun updateRoutine(routine: RoutineItem) {
        viewModelScope.launch {
            routineRepository.updateRoutine(routine)
        }
    }

    fun deleteRoutine(routine: RoutineItem) {
        viewModelScope.launch {
            routineRepository.deleteRoutine(routine)
        }
    }

    fun toggleRoutineComplete(routine: RoutineItem, isCompleted: Boolean) {
        viewModelScope.launch {
            val todayEpoch = LocalDate.now().toEpochDay()
            routineRepository.toggleCompletion(routine.id, todayEpoch, isCompleted)
        }
    }

    // --- User Preferences Actions ---

    fun setThemeMode(mode: String) {
        viewModelScope.launch { preferencesManager.setThemeMode(mode) }
    }

    fun setDefaultPriority(priority: TaskPriority) {
        viewModelScope.launch { preferencesManager.setDefaultPriority(priority) }
    }

    fun setDefaultReminderMinutes(minutes: Int) {
        viewModelScope.launch { preferencesManager.setDefaultReminderMinutes(minutes) }
    }

    fun setFirstDayOfWeek(day: Int) {
        viewModelScope.launch { preferencesManager.setFirstDayOfWeek(day) }
    }

    fun setTimeFormat24h(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setTimeFormat24h(enabled) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setNotificationsEnabled(enabled) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setSoundEnabled(enabled) }
    }

    fun setVibrateEnabled(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setVibrateEnabled(enabled) }
    }

    fun setUserName(name: String) {
        viewModelScope.launch { preferencesManager.setUserName(name) }
    }

    // --- Reset & Sample Data ---

    fun resetToSampleData() {
        viewModelScope.launch(Dispatchers.IO) {
            val db = (getApplication() as DayTaskApplication).database
            db.taskDao().clearAll()
            db.categoryDao().clearAll()
            db.routineDao().clearAllRoutines()
            db.routineDao().clearAllCompletions()
            AppDatabase.populateDatabase(db)
        }
    }

    suspend fun generateExportJson(): String {
        return withContext(Dispatchers.IO) {
            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
            val adapter = moshi.adapter(BackupData::class.java).indent("  ")
            val backup = BackupData(
                tasks = tasks.value,
                categories = categories.value,
                routines = routines.value,
                routineCompletions = routineCompletions.value
            )
            adapter.toJson(backup)
        }
    }

    // ViewModel Factory
    companion object {
        fun Factory(application: DayTaskApplication): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MainViewModel(
                        application = application,
                        taskRepository = application.taskRepository,
                        categoryRepository = application.categoryRepository,
                        routineRepository = application.routineRepository,
                        preferencesManager = application.preferencesManager,
                        alarmScheduler = application.alarmScheduler
                    ) as T
                }
            }
    }
}
