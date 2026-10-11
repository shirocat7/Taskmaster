package com.example.taskmaster.ui.viewmodel

import com.example.taskmaster.data.local.Task

data class TaskUiState(
    val activeDeck: List<Task> = emptyList(),
    val allTasks: List<Task> = emptyList(), // Added to display all daily tasks in the list sheet
    val currentTask: Task? = null,
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val progress: Float = 0f,
    val quote: String = "",
    val isDeckEmpty: Boolean = false,
    val isLoading: Boolean = true
)