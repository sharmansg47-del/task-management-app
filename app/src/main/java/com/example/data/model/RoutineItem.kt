package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "routines")
data class RoutineItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val timeOfDay: String = "Morning", // "Morning", "Afternoon", "Evening", "Night"
    val scheduledTime: String = "08:00 AM",
    val categoryName: String = "Personal",
    val iconName: String = "CheckCircle",
    val targetDays: String = "1,2,3,4,5,6,7", // 1=Mon..7=Sun
    val streakCount: Int = 0,
    val createdAtMillis: Long = System.currentTimeMillis()
) {
    companion object {
        fun defaultRoutines(): List<RoutineItem> = listOf(
            RoutineItem(id = 1, title = "Wake up & Drink 500ml water", timeOfDay = "Morning", scheduledTime = "06:30 AM", categoryName = "Fitness", iconName = "WaterDrop"),
            RoutineItem(id = 2, title = "Morning exercise & stretching", timeOfDay = "Morning", scheduledTime = "07:00 AM", categoryName = "Fitness", iconName = "FitnessCenter"),
            RoutineItem(id = 3, title = "Healthy breakfast & vitamins", timeOfDay = "Morning", scheduledTime = "08:00 AM", categoryName = "Personal", iconName = "Restaurant"),
            RoutineItem(id = 4, title = "Review daily priorities & plan", timeOfDay = "Morning", scheduledTime = "08:30 AM", categoryName = "Study", iconName = "MenuBook"),
            RoutineItem(id = 5, title = "Football practice / workout", timeOfDay = "Evening", scheduledTime = "05:30 PM", categoryName = "Football", iconName = "SportsSoccer"),
            RoutineItem(id = 6, title = "Dinner with family", timeOfDay = "Evening", scheduledTime = "07:30 PM", categoryName = "Personal", iconName = "Home"),
            RoutineItem(id = 7, title = "Read for 30 minutes", timeOfDay = "Evening", scheduledTime = "09:30 PM", categoryName = "Study", iconName = "AutoStories"),
            RoutineItem(id = 8, title = "Review completed tasks & prepare for tomorrow", timeOfDay = "Night", scheduledTime = "10:30 PM", categoryName = "Projects", iconName = "TaskAlt")
        )
    }
}

@JsonClass(generateAdapter = true)
@Entity(tableName = "routine_completions", primaryKeys = ["routineId", "dateEpochDay"])
data class RoutineCompletion(
    val routineId: Long,
    val dateEpochDay: Long, // LocalDate.toEpochDay()
    val completedAtMillis: Long = System.currentTimeMillis()
)
