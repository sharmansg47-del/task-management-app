package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.RoutineCompletion
import com.example.data.model.RoutineItem
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routines ORDER BY id ASC")
    fun getAllRoutines(): Flow<List<RoutineItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutines(routines: List<RoutineItem>)

    @Update
    suspend fun updateRoutine(routine: RoutineItem)

    @Delete
    suspend fun deleteRoutine(routine: RoutineItem)

    @Query("DELETE FROM routines WHERE id = :id")
    suspend fun deleteRoutineById(id: Long)

    @Query("SELECT * FROM routine_completions WHERE dateEpochDay = :dateEpochDay")
    fun getCompletionsForDate(dateEpochDay: Long): Flow<List<RoutineCompletion>>

    @Query("SELECT * FROM routine_completions WHERE routineId = :routineId")
    fun getCompletionsForRoutine(routineId: Long): Flow<List<RoutineCompletion>>

    @Query("SELECT * FROM routine_completions")
    fun getAllCompletions(): Flow<List<RoutineCompletion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: RoutineCompletion)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletions(completions: List<RoutineCompletion>)

    @Query("DELETE FROM routine_completions WHERE routineId = :routineId AND dateEpochDay = :dateEpochDay")
    suspend fun deleteCompletion(routineId: Long, dateEpochDay: Long)

    @Query("SELECT * FROM routines")
    suspend fun getAllRoutinesDirect(): List<RoutineItem>

    @Query("SELECT * FROM routine_completions")
    suspend fun getAllCompletionsDirect(): List<RoutineCompletion>

    @Query("DELETE FROM routines")
    suspend fun clearAllRoutines()

    @Query("DELETE FROM routine_completions")
    suspend fun clearAllCompletions()
}
