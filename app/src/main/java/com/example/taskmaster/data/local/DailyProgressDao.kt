package com.example.taskmaster.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyProgressDao {

    @Query("SELECT * FROM daily_progress ORDER BY dateMillis ASC")
    fun getAllProgress(): Flow<List<DailyProgress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: DailyProgress)
}