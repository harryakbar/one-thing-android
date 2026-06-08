package dev.harryakbar.onething.ui.streak

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.harryakbar.onething.data.DailyTask
import dev.harryakbar.onething.repository.TaskRepository
import dev.harryakbar.onething.utils.StreakCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class StreakUiState(
    val currentStreak: Int = 0,
    val last30Days: List<DayStatus> = emptyList()
)

data class DayStatus(
    val date: LocalDate,
    val isCompleted: Boolean,
    val isToday: Boolean
)

@HiltViewModel
class StreakViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val _uiState = MutableStateFlow(StreakUiState())
    val uiState: StateFlow<StreakUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllTasks().collect { tasks ->
                _uiState.value = buildUiState(tasks)
            }
        }
    }

    private fun buildUiState(tasks: List<DailyTask>): StreakUiState {
        val today = LocalDate.now()
        val completedDates = tasks
            .filter { it.isCompleted }
            .mapNotNull { runCatching { LocalDate.parse(it.date, formatter) }.getOrNull() }
            .toSet()

        val last30Days = (29 downTo 0).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong())
            DayStatus(
                date = date,
                isCompleted = completedDates.contains(date),
                isToday = date == today
            )
        }

        return StreakUiState(
            currentStreak = StreakCalculator.calculate(tasks, today),
            last30Days = last30Days
        )
    }
}
