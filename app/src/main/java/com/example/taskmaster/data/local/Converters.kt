package com.example.taskmaster.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStatus(status: TaskStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): TaskStatus = TaskStatus.valueOf(value)
}