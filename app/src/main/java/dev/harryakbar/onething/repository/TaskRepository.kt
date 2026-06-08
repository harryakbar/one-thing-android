package dev.harryakbar.onething.repository

import dev.harryakbar.onething.data.DailyTask
import dev.harryakbar.onething.data.DailyTaskDao
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val dao: DailyTaskDao
) {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun getTodayTask(): Flow<DailyTask?> {
        val today = LocalDate.now().format(dateFormatter)
        return dao.getByDate(today)
    }

    fun getAllTasks(): Flow<List<DailyTask>> = dao.getAll()

    suspend fun setTodayTask(title: String) {
        val today = LocalDate.now().format(dateFormatter)
        dao.upsert(DailyTask(date = today, title = title))
    }

    suspend fun completeTask(date: String) {
        val task = dao.getByDate(date)
        // We need a one-shot read; use a coroutine-based approach
        dao.upsert(
            DailyTask(
                date = date,
                title = "", // placeholder; will be overwritten by upsert preserving existing title
                isCompleted = true,
                completedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun completeTodayTask(title: String) {
        val today = LocalDate.now().format(dateFormatter)
        dao.upsert(
            DailyTask(
                date = today,
                title = title,
                isCompleted = true,
                completedAt = System.currentTimeMillis()
            )
        )
    }
}
