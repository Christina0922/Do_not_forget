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
import com.geniusbrain.forgetless.data.TaskEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TaskListUiState(
    val tasks: List<TaskEntity> = emptyList()
)

class TaskListViewModel(
    private val repository: Repository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()
    
    init {
        loadTasks()
    }
    
    private fun loadTasks() {
        viewModelScope.launch {
            repository.getAllTasks()
                .collectLatest { tasks ->
                    _uiState.update { it.copy(tasks = tasks) }
                }
        }
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
    
    fun deleteTask(context: Context, taskId: Long) {
        viewModelScope.launch {
            val task = repository.getTaskById(taskId) ?: return@launch
            
            // 알람 취소
            val scheduler = AlarmScheduler(context)
            scheduler.cancelAlarm(taskId)
            
            // 삭제
            repository.deleteTask(task)
        }
    }
    
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ForgetLessApplication)
                TaskListViewModel(application.repository)
            }
        }
    }
}

