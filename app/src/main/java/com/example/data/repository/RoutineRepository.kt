package com.example.data.repository

import com.example.data.local.RoutineDao
import com.example.data.model.RoutineCompletion
import com.example.data.model.RoutineItem
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class RoutineRepository(private val routineDao: RoutineDao) {
    val allRoutines: Flow<List<RoutineItem>> = routineDao.getAllRoutines()
    val allCompletions: Flow<List<RoutineCompletion>> = routineDao.getAllCompletions()

    fun getCompletionsForDate(dateEpochDay: Long): Flow<List<RoutineCompletion>> =
        routineDao.getCompletionsForDate(dateEpochDay)

    suspend fun insertRoutine(routine: RoutineItem): Long = routineDao.insertRoutine(routine)

    suspend fun insertRoutines(routines: List<RoutineItem>) = routineDao.insertRoutines(routines)

    suspend fun updateRoutine(routine: RoutineItem) = routineDao.updateRoutine(routine)

    suspend fun deleteRoutine(routine: RoutineItem) = routineDao.deleteRoutine(routine)

    suspend fun deleteRoutineById(id: Long) = routineDao.deleteRoutineById(id)

    suspend fun toggleCompletion(routineId: Long, dateEpochDay: Long, isCompleted: Boolean) {
        if (isCompleted) {
            routineDao.insertCompletion(
                RoutineCompletion(routineId = routineId, dateEpochDay = dateEpochDay)
            )
        } else {
            routineDao.deleteCompletion(routineId = routineId, dateEpochDay = dateEpochDay)
        }
    }

    suspend fun calculateRoutineStreak(routineId: Long): Int {
        val completions = routineDao.getAllCompletionsDirect().filter { it.routineId == routineId }
        val completionDates = completions.map { it.dateEpochDay }.toSet()
        var streak = 0
        var checkDate = LocalDate.now()

        // If today is completed, start counting from today. Otherwise, if yesterday was completed, start counting from yesterday.
        if (completionDates.contains(checkDate.toEpochDay())) {
            streak++
            checkDate = checkDate.minusDays(1)
        } else if (completionDates.contains(checkDate.minusDays(1).toEpochDay())) {
            checkDate = checkDate.minusDays(1)
            streak++
            checkDate = checkDate.minusDays(1)
        } else {
            return 0
        }

        while (completionDates.contains(checkDate.toEpochDay())) {
            streak++
            checkDate = checkDate.minusDays(1)
        }
        return streak
    }

    suspend fun getAllRoutinesDirect(): List<RoutineItem> = routineDao.getAllRoutinesDirect()

    suspend fun getAllCompletionsDirect(): List<RoutineCompletion> = routineDao.getAllCompletionsDirect()

    suspend fun clearAll() {
        routineDao.clearAllRoutines()
        routineDao.clearAllCompletions()
    }
}
