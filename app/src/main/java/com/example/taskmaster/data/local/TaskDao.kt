package com.example.taskmaster.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY orderInDeck ASC, id ASC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE dateCreated >= :startOfDay ORDER BY orderInDeck ASC")
    fun getTodayTasks(startOfDay: Long): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE status IN ('ACTIVE', 'DELAYED') ORDER BY orderInDeck ASC")
    fun getActiveDeck(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: Long)

    @Update
    suspend fun updateTasks(tasks: List<Task>)

    @Update
    suspend fun updateTask(task: Task)

    @Query("UPDATE tasks SET orderInDeck = :newOrder WHERE id = :taskId")
    suspend fun updateTaskOrder(taskId: Long, newOrder: Int)

    @Query("UPDATE tasks SET status = 'ACTIVE'")
    suspend fun resetAllTasksToActive()

    @Query("UPDATE tasks SET status = :status WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: Long, status: TaskStatus)

    @Query("UPDATE tasks SET title = :newTitle WHERE id = :taskId")
    suspend fun updateTaskTitle(taskId: Long, newTitle: String)
}
