package com.example.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.model.TaskItem
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    fun scheduleTaskReminder(task: TaskItem) {
        val triggerMillis = computeAlarmTriggerMillis(task) ?: return
        if (triggerMillis <= System.currentTimeMillis()) return

        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            putExtra(NotificationHelper.EXTRA_TASK_ID, task.id)
            putExtra(NotificationHelper.EXTRA_TASK_TITLE, task.title)
            putExtra("extra_description", task.description)
            putExtra("extra_priority", task.priority.title)
            val timeStr = if (task.dueTimeHour != null && task.dueTimeMinute != null) {
                String.format("%02d:%02d", task.dueTimeHour, task.dueTimeMinute)
            } else ""
            putExtra("extra_time", timeStr)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerMillis,
                            pendingIntent
                        )
                    } else {
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerMillis,
                            pendingIntent
                        )
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerMillis,
                        pendingIntent
                    )
                }
            }
        } catch (_: SecurityException) {
            // In case exact alarm permission was revoked
        }
    }

    fun cancelTaskReminder(taskId: Long) {
        val intent = Intent(context, TaskReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager?.cancel(pendingIntent)
        NotificationHelper.cancelNotification(context, taskId)
    }

    fun snoozeTaskReminder(taskId: Long, title: String, snoozeMinutes: Int = 10) {
        val triggerMillis = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000L)
        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            putExtra(NotificationHelper.EXTRA_TASK_ID, taskId)
            putExtra(NotificationHelper.EXTRA_TASK_TITLE, title)
            putExtra("extra_description", "Snoozed reminder ($snoozeMinutes min)")
            putExtra("extra_priority", "Snoozed")
            putExtra("extra_time", "Now")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager?.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerMillis,
                pendingIntent
            )
        } catch (_: SecurityException) {
            alarmManager?.set(
                AlarmManager.RTC_WAKEUP,
                triggerMillis,
                pendingIntent
            )
        }
    }

    private fun computeAlarmTriggerMillis(task: TaskItem): Long? {
        if (task.reminderExactMillis != null) {
            return task.reminderExactMillis
        }
        val dueDate = task.dueDate ?: return null
        val hour = task.dueTimeHour ?: 9
        val minute = task.dueTimeMinute ?: 0
        val localDate = LocalDate.ofEpochDay(dueDate)
        val localTime = LocalTime.of(hour, minute)
        val targetDateTime = LocalDateTime.of(localDate, localTime)
        val zoneMillis = targetDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val minutesBefore = task.reminderMinutesBefore ?: 0
        return zoneMillis - (minutesBefore * 60 * 1000L)
    }
}
