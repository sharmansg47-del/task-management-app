package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TaskReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(NotificationHelper.EXTRA_TASK_ID, -1L)
        if (taskId == -1L) return

        val title = intent.getStringExtra(NotificationHelper.EXTRA_TASK_TITLE) ?: "Task Reminder"
        val description = intent.getStringExtra("extra_description") ?: ""
        val priority = intent.getStringExtra("extra_priority") ?: "Normal"
        val timeInfo = intent.getStringExtra("extra_time") ?: ""

        NotificationHelper.showTaskNotification(
            context = context,
            taskId = taskId,
            title = title,
            description = description,
            priority = priority,
            timeInfo = timeInfo
        )
    }
}
