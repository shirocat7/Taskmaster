package com.example.taskmaster.data.repository

import android.content.Context
import com.example.taskmaster.data.local.Task
import com.example.taskmaster.data.local.TaskDao
import com.example.taskmaster.data.local.TaskStatus
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class TaskRepository(private val taskDao: TaskDao, context: Context) {

    private val prefs = context.getSharedPreferences("deck_prefs", Context.MODE_PRIVATE)
    val todayTasks: Flow<List<Task>> = taskDao.getAllTasks()
    val activeDeck: Flow<List<Task>> = taskDao.getActiveDeck()

    /**
     * Checks if midnight has passed since the last launch.
     * If yes, resets all tasks back to ACTIVE.
     */
    suspend fun checkAndResetDailyTasks() {
        val lastOpenDay = prefs.getInt("last_open_day_of_year", -1)
        val lastOpenYear = prefs.getInt("last_open_year", -1)

        Calendar.getInstance().apply {
            val currentDay = get(Calendar.DAY_OF_YEAR)
            val currentYear = get(Calendar.YEAR)

            // If it's a new day or year, reset the task deck!
            if (currentDay != lastOpenDay || currentYear != lastOpenYear) {
                taskDao.resetAllTasksToActive()

                // Update stored last-opened date
                prefs.edit()
                    .putInt("last_open_day_of_year", currentDay)
                    .putInt("last_open_year", currentYear)
                    .apply()
            }
        }
    }
    private fun getStartOfDayMillis(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    suspend fun addTask(title: String) {
        val newTask = Task(title = title)
        taskDao.insertTask(newTask)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task);
    }
    suspend fun deleteTask(taskId: Long) {
        taskDao.deleteTask(taskId)
    }

    suspend fun updateStatus(taskId: Long, status: TaskStatus) {
        taskDao.updateTaskStatus(taskId, status)
    }

    suspend fun updateTaskTitle(taskId: Long, newTitle: String) {
        taskDao.updateTaskTitle(taskId, newTitle)
    }

    suspend fun delayTask(task: Task, currentMaxOrder: Int) {
        // Move card to the back of the deck by increasing its order index
        val updatedTask = task.copy(
            status = TaskStatus.DELAYED,
            orderInDeck = currentMaxOrder + 1
        )
        taskDao.updateTask(updatedTask)
    }

    suspend fun reorderTasks(fromIndex: Int, toIndex: Int, currentList: List<Task>) {
        if (fromIndex !in currentList.indices || toIndex !in currentList.indices) return

        val mutableList = currentList.toMutableList()
        val movedTask = mutableList.removeAt(fromIndex)
        mutableList.add(toIndex, movedTask)

        // Re-index orderInDeck sequentially
        val updatedTasks = mutableList.mapIndexed { index, task ->
            task.copy(orderInDeck = index)
        }

        taskDao.updateTasks(updatedTasks)
    }
}