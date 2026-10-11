package com.example.taskmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.taskmaster.data.local.TaskDatabase

import com.example.taskmaster.data.repository.TaskRepository
import com.example.taskmaster.ui.TaskDeckScreen
import com.example.taskmaster.ui.theme.TaskMasterTheme
import com.example.taskmaster.ui.viewmodel.TaskViewModel
import com.example.taskmaster.ui.viewmodel.TaskViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Database, Repository, and ViewModel
        val database = TaskDatabase.getDatabase(applicationContext)
        val repository = TaskRepository(database.taskDao(), database.dailyProgressDao(),applicationContext)
        val viewModel: TaskViewModel by viewModels { TaskViewModelFactory(repository) }

        setContent {
            TaskMasterTheme {
                // Renders your Jetpack Compose screen full screen
                TaskDeckScreen(viewModel = viewModel)
            }
        }
    }
}