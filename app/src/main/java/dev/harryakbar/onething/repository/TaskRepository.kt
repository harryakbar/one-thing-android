package dev.harryakbar.onething.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.harryakbar.onething.data.DailyTask
import dev.harryakbar.onething.data.DailyTaskDao
import dev.harryakbar.onething.widget.WidgetUpdater
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val dao: DailyTaskDao,
    @ApplicationContext private val context: Context
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
        WidgetUpdater.update(context)
    }

    suspend fun completeTask(date: String) {
        dao.upsert(
            DailyTask(
                date = date,
                title = "",
                isCompleted = true,
                completedAt = System.currentTimeMillis()
            )
        )
        WidgetUpdater.update(context)
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
        WidgetUpdater.update(context)
    }
}
