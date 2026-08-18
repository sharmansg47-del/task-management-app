package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.DayTaskApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskNotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(NotificationHelper.EXTRA_TASK_ID, -1L)
        if (taskId == -1L) return

        val app = context.applicationContext as? DayTaskApplication ?: return
        val taskRepo = app.taskRepository
        val alarmScheduler = app.alarmScheduler

        when (intent.action) {
            NotificationHelper.ACTION_COMPLETE_TASK -> {
                NotificationHelper.cancelNotification(context, taskId)
                CoroutineScope(Dispatchers.IO).launch {
                    val task = taskRepo.getTaskById(taskId)
                    if (task != null) {
                        taskRepo.setTaskCompleted(task, true)
                        alarmScheduler.cancelTaskReminder(taskId)
                    }
                }
            }
            NotificationHelper.ACTION_SNOOZE_TASK -> {
                NotificationHelper.cancelNotification(context, taskId)
                val title = intent.getStringExtra(NotificationHelper.EXTRA_TASK_TITLE) ?: "Task Reminder"
                alarmScheduler.snoozeTaskReminder(taskId, title, 10)
            }
        }
    }
}
