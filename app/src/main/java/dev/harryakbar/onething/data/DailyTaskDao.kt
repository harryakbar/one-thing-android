package dev.harryakbar.onething.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyTaskDao {
    @Query("SELECT * FROM daily_tasks WHERE date = :date LIMIT 1")
    fun getByDate(date: String): Flow<DailyTask?>

    @Query("SELECT * FROM daily_tasks ORDER BY date DESC")
    fun getAll(): Flow<List<DailyTask>>

    @Upsert
    suspend fun upsert(task: DailyTask)
}
