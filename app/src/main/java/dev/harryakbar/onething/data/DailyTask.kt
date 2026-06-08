package dev.harryakbar.onething.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_tasks")
data class DailyTask(
    @PrimaryKey val date: String, // "yyyy-MM-dd" format, one per day
    val title: String,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null // epoch millis
)
