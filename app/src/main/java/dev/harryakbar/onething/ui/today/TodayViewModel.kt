package dev.harryakbar.onething.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.harryakbar.onething.data.DailyTask
import dev.harryakbar.onething.repository.TaskRepository
import dev.harryakbar.onething.utils.StreakCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TodayUiState {
    object Loading : TodayUiState()
    object Empty : TodayUiState()
    data class TaskSet(val task: DailyTask) : TodayUiState()
    data class Completed(val task: DailyTask, val streak: Int) : TodayUiState()
}

@HiltViewModel
class TodayViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TodayUiState>(TodayUiState.Loading)
    val uiState: StateFlow<TodayUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.getTodayTask(),
                repository.getAllTasks()
            ) { todayTask, allTasks ->
                when {
                    todayTask == null -> TodayUiState.Empty
                    todayTask.isCompleted -> {
                        val streak = StreakCalculator.calculate(allTasks)
                        TodayUiState.Completed(todayTask, streak)
                    }
                    else -> TodayUiState.TaskSet(todayTask)
                }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun setTask(title: String) {
        val trimmed = title.trim()
        if (trimmed.isBlank()) return
        viewModelScope.launch {
            repository.setTodayTask(trimmed)
        }
    }

    fun completeTask(task: DailyTask) {
        viewModelScope.launch {
            repository.completeTodayTask(task.title)
        }
    }
}
