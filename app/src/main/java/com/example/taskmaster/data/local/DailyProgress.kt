package com.example.taskmaster.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_progress")
data class DailyProgress(
    @PrimaryKey val dateMillis: Long, // Unique timestamp representing the start of that day (Midnight)
    val completedCount: Int,
    val totalCount: Int
)