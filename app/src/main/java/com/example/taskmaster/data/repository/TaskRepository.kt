package com.example.taskmaster.data.repository

import com.example.taskmaster.data.local.Task
import com.example.taskmaster.data.local.TaskDao
import com.example.taskmaster.data.local.TaskStatus
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class TaskRepository(private val taskDao: TaskDao) {

    private fun getStartOfDayMillis(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    val todayTasks: Flow<List<Task>> = taskDao.getTodayTasks(getStartOfDayMillis())
    val activeDeck: Flow<List<Task>> = taskDao.getActiveDeck(getStartOfDayMillis())

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
}