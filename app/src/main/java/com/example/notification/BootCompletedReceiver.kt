package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.DayTaskApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            val app = context.applicationContext as? DayTaskApplication ?: return
            val taskRepo = app.taskRepository
            val alarmScheduler = app.alarmScheduler

            CoroutineScope(Dispatchers.IO).launch {
                val now = System.currentTimeMillis()
                val activeReminders = taskRepo.getActiveReminders(now)
                activeReminders.forEach { task ->
                    alarmScheduler.scheduleTaskReminder(task)
                }
            }
        }
    }
}
