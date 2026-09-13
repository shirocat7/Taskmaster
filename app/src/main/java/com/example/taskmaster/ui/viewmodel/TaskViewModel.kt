package com.example.taskmaster.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskmaster.data.local.Task
import com.example.taskmaster.data.local.TaskStatus
import com.example.taskmaster.data.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    init {
        // Run daily reset check immediately when ViewModel is created
        viewModelScope.launch {
            repository.checkAndResetDailyTasks()
        }
    }

    // Daily Motivational Quotes List
    private val defaultQuotes = listOf(
        "Focus on being productive instead of busy.",
        "Small daily improvements over time lead to stunning results.",
        "Do the hard jobs first. The easy jobs will take care of themselves.",
        "Action is the foundational key to all success."
    )

    // Update uiState flow mapping to pass allTasks
    val uiState: StateFlow<TaskUiState> = combine(
        repository.todayTasks,
        repository.activeDeck
    ) { allTasks, activeDeck ->
        val total = allTasks.size
        val completed = allTasks.count { it.status == TaskStatus.COMPLETED }
        val progressFraction = if (total > 0) completed.toFloat() / total.toFloat() else 0f

        TaskUiState(
            activeDeck = activeDeck,
            allTasks = allTasks,
            currentTask = activeDeck.firstOrNull(),
            completedCount = completed,
            totalCount = total,
            progress = progressFraction,
            isDeckEmpty = activeDeck.isEmpty() && total > 0,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TaskUiState())

    // --- Action Handlers Triggered by Gestures & Buttons ---

    /**
     * Action: FINISH (Swipe Right)
     * Marks the current card as COMPLETED and updates overall progress.
     */
    fun completeCurrentTask() {
        val current = uiState.value.currentTask ?: return
        viewModelScope.launch {
            repository.updateStatus(current.id, TaskStatus.COMPLETED)
        }
    }

    /**
     * Action: CANCEL (Swipe Left)
     * Skips the task for today without deleting it from historical records.
     */
    fun cancelCurrentTask() {
        val current = uiState.value.currentTask ?: return
        viewModelScope.launch {
            repository.updateStatus(current.id, TaskStatus.CANCELED)
        }
    }

    /**
     * Action: DELAY (Swipe Up)
     * Postpones the card by sending it to the back of the current deck stack.
     */
    fun delayCurrentTask() {
        val current = uiState.value.currentTask ?: return
        val currentMaxOrder = uiState.value.activeDeck.maxOfOrNull { it.orderInDeck } ?: 0
        viewModelScope.launch {
            repository.delayTask(current, currentMaxOrder)
        }
    }

    /**
     * Quick Add: Insert a new task directly into today's deck.
     */
    fun addNewTask(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addTask(title.trim())
        }
    }

    /**
     * Quick Delete: Insert a new task directly into today's deck.
     */
    fun deleteCurrentTask() {
        val current = uiState.value.currentTask ?: return
        viewModelScope.launch {
            repository.deleteTask(current)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    // Action Handlers
    fun editTaskTitle(taskId: Long, newTitle: String) {
        if (newTitle.isBlank()) return
        viewModelScope.launch {
            repository.updateTaskTitle(taskId, newTitle.trim())
        }
    }



}

// Simple Factory for instantiating TaskViewModel without Hilt DI
class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}