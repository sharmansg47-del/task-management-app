package com.example.data.repository

import com.example.data.local.TaskDao
import com.example.data.model.RepeatFrequency
import com.example.data.model.TaskItem
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.LocalDate

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: Flow<List<TaskItem>> = taskDao.getAllTasks()
    val completedTasks: Flow<List<TaskItem>> = taskDao.getCompletedTasks()

    suspend fun getTaskById(id: Long): TaskItem? = taskDao.getTaskById(id)

    fun getTasksForDate(epochDay: Long): Flow<List<TaskItem>> = taskDao.getTasksForDate(epochDay)

    fun getTasksForDateRange(startEpochDay: Long, endEpochDay: Long): Flow<List<TaskItem>> =
        taskDao.getTasksForDateRange(startEpochDay, endEpochDay)

    suspend fun getActiveReminders(currentTimeMillis: Long): List<TaskItem> =
        taskDao.getActiveReminders(currentTimeMillis)

    suspend fun insertTask(task: TaskItem): Long = taskDao.insertTask(task)

    suspend fun insertTasks(tasks: List<TaskItem>) = taskDao.insertTasks(tasks)

    suspend fun updateTask(task: TaskItem) = taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskItem) = taskDao.deleteTask(task)

    suspend fun deleteTaskById(id: Long) = taskDao.deleteTaskById(id)

    suspend fun setTaskCompleted(task: TaskItem, isCompleted: Boolean) {
        val completedAt = if (isCompleted) System.currentTimeMillis() else null
        taskDao.updateTaskCompletion(task.id, isCompleted, completedAt)

        // If completing a recurring task, automatically generate the next occurrence!
        if (isCompleted && task.repeatFrequency != RepeatFrequency.NONE) {
            generateNextRecurringTask(task)
        }
    }

    private suspend fun generateNextRecurringTask(task: TaskItem) {
        val baseDate = task.dueDate?.let { LocalDate.ofEpochDay(it) } ?: LocalDate.now()
        val nextDate = computeNextRecurringDate(baseDate, task.repeatFrequency, task.repeatCustomDays)

        val nextTask = task.copy(
            id = 0, // new ID
            dueDate = nextDate.toEpochDay(),
            isCompleted = false,
            completedAtMillis = null,
            createdAtMillis = System.currentTimeMillis(),
            parentRecurringId = if (task.parentRecurringId != null) task.parentRecurringId else task.id
        )
        taskDao.insertTask(nextTask)
    }

    private fun computeNextRecurringDate(
        currentDate: LocalDate,
        frequency: RepeatFrequency,
        customDaysStr: String
    ): LocalDate {
        return when (frequency) {
            RepeatFrequency.NONE -> currentDate
            RepeatFrequency.DAILY -> currentDate.plusDays(1)
            RepeatFrequency.WEEKDAYS -> {
                var next = currentDate.plusDays(1)
                while (next.dayOfWeek == DayOfWeek.SATURDAY || next.dayOfWeek == DayOfWeek.SUNDAY) {
                    next = next.plusDays(1)
                }
                next
            }
            RepeatFrequency.WEEKENDS -> {
                var next = currentDate.plusDays(1)
                while (next.dayOfWeek != DayOfWeek.SATURDAY && next.dayOfWeek != DayOfWeek.SUNDAY) {
                    next = next.plusDays(1)
                }
                next
            }
            RepeatFrequency.WEEKLY -> currentDate.plusWeeks(1)
            RepeatFrequency.MONTHLY -> currentDate.plusMonths(1)
            RepeatFrequency.CUSTOM -> {
                val parsedDays = customDaysStr.split(",")
                    .mapNotNull { it.trim().toIntOrNull() }
                    .filter { it in 1..7 }
                if (parsedDays.isEmpty()) {
                    currentDate.plusDays(1)
                } else {
                    var next = currentDate.plusDays(1)
                    for (i in 1..14) {
                        if (parsedDays.contains(next.dayOfWeek.value)) {
                            return next
                        }
                        next = next.plusDays(1)
                    }
                    currentDate.plusDays(1)
                }
            }
        }
    }

    suspend fun getAllTasksDirect(): List<TaskItem> = taskDao.getAllTasksDirect()

    suspend fun clearAll() = taskDao.clearAll()
}
