package com.example.taskmaster.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskStatus {
    ACTIVE,
    COMPLETED,
    DELAYED,
    CANCELED
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val dateCreated: Long = System.currentTimeMillis(),
    val status: TaskStatus = TaskStatus.ACTIVE,
    val orderInDeck: Int = 0 // Used to shuffle or postpone delayed cards to the back
)
