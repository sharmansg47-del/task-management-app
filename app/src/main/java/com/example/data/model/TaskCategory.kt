package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "categories")
data class TaskCategory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val iconName: String = "Label",
    val colorHex: String = "#3F51B5",
    val isDefault: Boolean = false
) {
    companion object {
        fun defaultCategories(): List<TaskCategory> = listOf(
            TaskCategory(id = 1, name = "College", iconName = "School", colorHex = "#8B5CF6", isDefault = true),
            TaskCategory(id = 2, name = "Personal", iconName = "Home", colorHex = "#06B6D4", isDefault = true),
            TaskCategory(id = 3, name = "Football", iconName = "SportsSoccer", colorHex = "#10B981", isDefault = true),
            TaskCategory(id = 4, name = "Fitness", iconName = "FitnessCenter", colorHex = "#F59E0B", isDefault = true),
            TaskCategory(id = 5, name = "Projects", iconName = "Code", colorHex = "#3B82F6", isDefault = true),
            TaskCategory(id = 6, name = "Work", iconName = "Work", colorHex = "#6366F1", isDefault = true),
            TaskCategory(id = 7, name = "Study", iconName = "MenuBook", colorHex = "#EC4899", isDefault = true),
            TaskCategory(id = 8, name = "Other", iconName = "Category", colorHex = "#64748B", isDefault = true)
        )
    }
}
