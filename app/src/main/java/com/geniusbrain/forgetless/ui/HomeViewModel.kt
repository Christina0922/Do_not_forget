package com.geniusbrain.forgetless.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.geniusbrain.forgetless.ForgetLessApplication
import com.geniusbrain.forgetless.alarm.AlarmScheduler
import com.geniusbrain.forgetless.data.Repository
import com.geniusbrain.forgetless.data.TaskDailyStatusEntity
import com.geniusbrain.forgetless.data.TaskEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

data class TaskWithStatus(
    val task: TaskEntity,
    val status: TaskDailyStatusEntity?
)

data class HomeUiState(
    val todayTasks: List<TaskWithStatus> = emptyList()
)

class HomeViewModel(
    private val repository: Repository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val todayDateKey = Repository.getTodayDateKey()
    private val todayDayOfWeek = LocalDate.now().dayOfWeek.value // 월=1, 일=7
    
    init {
        loadTodayTasks()
    }
    
    private fun loadTodayTasks() {
        viewModelScope.launch {
            repository.getAllTasks()
                .collectLatest { allTasks ->
                    // 오늘 요일에 해당하는 enabled=true 항목만 필터링
                    val todayTasks = allTasks.filter { task ->
                        task.enabled && isTaskForToday(task)
                    }
                    
                    // 각 항목의 상태 조회
                    val tasksWithStatus = todayTasks.map { task ->
                        val status = repository.getStatus(task.id, todayDateKey)
                        TaskWithStatus(task, status)
                    }
                    
                    _uiState.update { it.copy(todayTasks = tasksWithStatus) }
                }
        }
    }
    
    private fun isTaskForToday(task: TaskEntity): Boolean {
        val repeatDays = try {
            task.repeatDays.split(",").map { it.trim().toInt() }.toSet()
        } catch (e: Exception) {
            emptySet()
        }
        return todayDayOfWeek in repeatDays
    }
    
    fun toggleTaskEnabled(context: Context, taskId: Long, enabled: Boolean) {
        viewModelScope.launch {
            val task = repository.getTaskById(taskId) ?: return@launch
            repository.updateTask(task.copy(enabled = enabled))
            
            val scheduler = AlarmScheduler(context)
            if (enabled) {
                scheduler.scheduleAlarm(task.copy(enabled = true))
            } else {
                scheduler.cancelAlarm(taskId)
            }
        }
    }
    
    fun completeTask(taskId: Long) {
        viewModelScope.launch {
            repository.markAsCompleted(taskId, todayDateKey)
            loadTodayTasks() // 새로고침
        }
    }
    
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ForgetLessApplication)
                HomeViewModel(application.repository)
            }
        }
    }
}

