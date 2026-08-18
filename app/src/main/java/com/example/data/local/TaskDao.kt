package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.TaskItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, priority DESC, dueDate ASC, dueTimeHour ASC, dueTimeMinute ASC, id DESC")
    fun getAllTasks(): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: Long): TaskItem?

    @Query("SELECT * FROM tasks WHERE dueDate = :epochDay ORDER BY isCompleted ASC, priority DESC, dueTimeHour ASC, dueTimeMinute ASC")
    fun getTasksForDate(epochDay: Long): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE dueDate >= :startEpochDay AND dueDate <= :endEpochDay ORDER BY dueDate ASC, priority DESC")
    fun getTasksForDateRange(startEpochDay: Long, endEpochDay: Long): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY completedAtMillis DESC")
    fun getCompletedTasks(): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE reminderExactMillis IS NOT NULL AND reminderExactMillis > :currentTimeMillis AND isCompleted = 0")
    suspend fun getActiveReminders(currentTimeMillis: Long): List<TaskItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskItem>)

    @Update
    suspend fun updateTask(task: TaskItem)

    @Delete
    suspend fun deleteTask(task: TaskItem)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("UPDATE tasks SET isCompleted = :isCompleted, completedAtMillis = :completedAt WHERE id = :id")
    suspend fun updateTaskCompletion(id: Long, isCompleted: Boolean, completedAt: Long?)

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasksDirect(): List<TaskItem>

    @Query("DELETE FROM tasks")
    suspend fun clearAll()
}
