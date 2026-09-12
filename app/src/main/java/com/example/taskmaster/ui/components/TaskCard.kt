package com.example.taskmaster.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun TaskCard(taskTitle: String) {
    Text(text = "Task: $taskTitle")
}
