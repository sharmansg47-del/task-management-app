package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

enum class TaskPriority(val title: String, val level: Int) {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3),
    URGENT("Urgent", 4);

    companion object {
        fun fromString(value: String?): TaskPriority {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: MEDIUM
        }
    }
}

enum class RepeatFrequency(val title: String) {
    NONE("Does not repeat"),
    DAILY("Every day"),
    WEEKDAYS("Weekdays (Mon-Fri)"),
    WEEKENDS("Weekends (Sat-Sun)"),
    WEEKLY("Every week"),
    MONTHLY("Every month"),
    CUSTOM("Custom schedule");

    companion object {
        fun fromString(value: String?): RepeatFrequency {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: NONE
        }
    }
}

@JsonClass(generateAdapter = true)
@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDate: Long? = null, // epoch day (e.g. LocalDate.toEpochDay()) or null
    val dueTimeHour: Int? = null, // 0-23
    val dueTimeMinute: Int? = null, // 0-59
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val categoryId: Long? = null,
    val categoryName: String = "",
    val reminderMinutesBefore: Int? = null, // e.g., 0, 10, 30, 60, 1440
    val reminderExactMillis: Long? = null, // calculated alarm trigger timestamp
    val repeatFrequency: RepeatFrequency = RepeatFrequency.NONE,
    val repeatCustomDays: String = "", // e.g. "1,3,5" for Mon, Wed, Fri (1=Mon..7=Sun)
    val isCompleted: Boolean = false,
    val completedAtMillis: Long? = null,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val notes: String = "",
    val parentRecurringId: Long? = null
)
