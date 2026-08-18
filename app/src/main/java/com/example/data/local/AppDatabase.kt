package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.RepeatFrequency
import com.example.data.model.RoutineCompletion
import com.example.data.model.RoutineItem
import com.example.data.model.TaskCategory
import com.example.data.model.TaskItem
import com.example.data.model.TaskPriority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

@Database(
    entities = [TaskItem::class, TaskCategory::class, RoutineItem::class, RoutineCompletion::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
    abstract fun routineDao(): RoutineDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "daytask_database"
                )
                .addCallback(DatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database)
                    }
                }
            }
        }

        suspend fun populateDatabase(database: AppDatabase) {
            val categoryDao = database.categoryDao()
            val taskDao = database.taskDao()
            val routineDao = database.routineDao()

            // 1. Prepopulate Categories
            categoryDao.insertCategories(TaskCategory.defaultCategories())

            // 2. Prepopulate Routines
            routineDao.insertRoutines(RoutineItem.defaultRoutines())

            // 3. Prepopulate Starter Tasks
            val todayEpoch = LocalDate.now().toEpochDay()
            val tomorrowEpoch = LocalDate.now().plusDays(1).toEpochDay()
            val sampleTasks = listOf(
                TaskItem(
                    id = 1,
                    title = "Complete Java assignment",
                    description = "Finish the Room persistence and unit testing module",
                    dueDate = todayEpoch,
                    dueTimeHour = 14,
                    dueTimeMinute = 30,
                    priority = TaskPriority.HIGH,
                    categoryId = 1,
                    categoryName = "College",
                    reminderMinutesBefore = 15,
                    isCompleted = false,
                    notes = "Submit via student portal before 5 PM"
                ),
                TaskItem(
                    id = 2,
                    title = "Football practice",
                    description = "Team scrimmage and endurance drills at community field",
                    dueDate = todayEpoch,
                    dueTimeHour = 17,
                    dueTimeMinute = 0,
                    priority = TaskPriority.URGENT,
                    categoryId = 3,
                    categoryName = "Football",
                    repeatFrequency = RepeatFrequency.CUSTOM,
                    repeatCustomDays = "1,3,5",
                    reminderMinutesBefore = 30,
                    isCompleted = true,
                    completedAtMillis = System.currentTimeMillis() - 3600000,
                    notes = "Bring cleats, shin guards and water bottle"
                ),
                TaskItem(
                    id = 3,
                    title = "Work on project",
                    description = "Refactor app architecture and optimize startup performance",
                    dueDate = todayEpoch,
                    dueTimeHour = 19,
                    dueTimeMinute = 0,
                    priority = TaskPriority.MEDIUM,
                    categoryId = 5,
                    categoryName = "Projects",
                    reminderMinutesBefore = 10,
                    isCompleted = false,
                    notes = "Check Compose layout benchmarks"
                ),
                TaskItem(
                    id = 4,
                    title = "Read for 30 minutes",
                    description = "Read chapter 4 of Clean Architecture book",
                    dueDate = todayEpoch,
                    dueTimeHour = 21,
                    dueTimeMinute = 30,
                    priority = TaskPriority.LOW,
                    categoryId = 7,
                    categoryName = "Study",
                    repeatFrequency = RepeatFrequency.DAILY,
                    reminderMinutesBefore = 10,
                    isCompleted = false,
                    notes = "Take notes on dependency inversion"
                ),
                TaskItem(
                    id = 5,
                    title = "Grocery shopping & meal prep",
                    description = "Buy fresh vegetables, protein, and fruits for the week",
                    dueDate = tomorrowEpoch,
                    dueTimeHour = 11,
                    dueTimeMinute = 0,
                    priority = TaskPriority.MEDIUM,
                    categoryId = 2,
                    categoryName = "Personal",
                    reminderMinutesBefore = 30,
                    isCompleted = false
                ),
                TaskItem(
                    id = 6,
                    title = "Full body gym workout",
                    description = "Compound lifts: squats, bench press, deadlifts",
                    dueDate = tomorrowEpoch,
                    dueTimeHour = 16,
                    dueTimeMinute = 0,
                    priority = TaskPriority.HIGH,
                    categoryId = 4,
                    categoryName = "Fitness",
                    repeatFrequency = RepeatFrequency.CUSTOM,
                    repeatCustomDays = "2,4,6",
                    isCompleted = false
                )
            )
            taskDao.insertTasks(sampleTasks)

            // Prepopulate some routine completions for today for a great starter experience
            routineDao.insertCompletion(
                RoutineCompletion(routineId = 1, dateEpochDay = todayEpoch)
            )
            routineDao.insertCompletion(
                RoutineCompletion(routineId = 2, dateEpochDay = todayEpoch)
            )
        }
    }
}
