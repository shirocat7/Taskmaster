package com.example.taskmaster.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun TaskProgressBar(progress: Float) {
    Text(text = "Progress: ${progress * 100}%")
}
