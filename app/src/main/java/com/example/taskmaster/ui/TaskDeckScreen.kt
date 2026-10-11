package com.example.taskmaster.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskmaster.data.local.Task
import com.example.taskmaster.ui.viewmodel.TaskViewModel
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDeckScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var showListSheet by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    var newTaskTitle by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column {
                LinearProgressIndicator(
                    progress = { uiState.progress },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    // Button to slide open/close the task list
                    IconButton(
                        onClick = {
                            showListSheet = true},
                            modifier = Modifier.padding(top = 12.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.FormatListBulleted,
                            contentDescription = "View Task List"
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // 1. Quote Header TODO: Decide to keep or remove
//            Text(
//                text = "\"${uiState.quote}\"",
//                style = MaterialTheme.typography.titleMedium,
//                color = MaterialTheme.colorScheme.onSurfaceVariant,
//                textAlign = TextAlign.Center
//            )

            // --- GAP BETWEEN Quote AND Card ---
            Spacer(modifier = Modifier.height(32.dp))

            // 2. Active Card Area
            if (uiState.totalCount == 0) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "No cards in your deck today.", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap + to add your first task.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else if (uiState.isDeckEmpty) {
                Text(text = "🎉 All clear for today!", style = MaterialTheme.typography.headlineSmall)
            } else {
                uiState.currentTask?.let { task ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }

            // --- GAP BETWEEN CARD AND BUTTONS ---
            Spacer(modifier = Modifier.height(16.dp))

            // 3. Action Buttons
            if (!uiState.isDeckEmpty && uiState.totalCount > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(onClick = { viewModel.cancelCurrentTask() }) { Text("Cancel") }
                    OutlinedButton(onClick = { viewModel.delayCurrentTask() }) { Text("Delay") }
                    Button(onClick = { viewModel.completeCurrentTask() }) { Text("Done") }
                }
            } else {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }

    // --- SLIDING TASK LIST SHEET WITH REORDER CONTROLS ---
    if (showListSheet) {
        ModalBottomSheet(
            onDismissRequest = { showListSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Today's Tasks (${uiState.completedCount}/${uiState.totalCount})",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (uiState.allTasks.isEmpty()) {
                    Text("No tasks added yet.", color = MaterialTheme.colorScheme.outline)
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxHeight(0.6f)
                    ) {
                        itemsIndexed(uiState.allTasks, key = { _, task -> task.id }) { index, task ->
                            ListItem(
                                headlineContent = { Text(task.title) },
                                supportingContent = { Text("Status: ${task.status.name}") },
                                leadingContent = {
                                    // Up / Down Reorder Arrows
                                    Column {
                                        IconButton(
                                            onClick = { viewModel.moveTaskUp(index) },
                                            enabled = index > 0,
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowUpward,
                                                contentDescription = "Move Up"
                                            )
                                        }
                                        IconButton(
                                            onClick = { viewModel.moveTaskDown(index) },
                                            enabled = index < uiState.allTasks.lastIndex,
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowDownward,
                                                contentDescription = "Move Down"
                                            )
                                        }
                                    }
                                },
                                trailingContent = {
                                    Row {
                                        IconButton(onClick = { editingTask = task }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                                        }
                                        IconButton(onClick = { viewModel.deleteTask(task.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                                        }
                                    }
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }

    // --- ADD TASK DIALOG ---
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false; newTaskTitle = "" },
            title = { Text("Add New Card") },
            text = {
                OutlinedTextField(
                    value = newTaskTitle,
                    onValueChange = { newTaskTitle = it },
                    label = { Text("Task description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newTaskTitle.isNotBlank()) {
                            viewModel.addNewTask(newTaskTitle)
                            newTaskTitle = ""
                            showAddDialog = false
                        }
                    }
                ) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false; newTaskTitle = "" }) { Text("Cancel") }
            }
        )
    }

    // --- EDIT TASK DIALOG ---
    editingTask?.let { taskToEdit ->
        var updatedTitle by remember { mutableStateOf(taskToEdit.title) }

        AlertDialog(
            onDismissRequest = { editingTask = null },
            title = { Text("Edit Task") },
            text = {
                OutlinedTextField(
                    value = updatedTitle,
                    onValueChange = { updatedTitle = it },
                    label = { Text("Task description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.editTaskTitle(taskToEdit.id, updatedTitle)
                        editingTask = null
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { editingTask = null }) { Text("Cancel") }
            }
        )
    }
}