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

data class TaskEditUiState(
    val title: String = "",
    val hour: Int = 8,
    val minute: Int = 0,
    val selectedDays: Set<Int> = setOf(1, 2, 3, 4, 5), // 기본 월~금
    val note: String = "",
    val titleError: String? = null,
    val daysError: String? = null
)

class TaskEditViewModel(
    private val repository: Repository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TaskEditUiState())
    val uiState: StateFlow<TaskEditUiState> = _uiState.asStateFlow()
    
    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            val task = repository.getTaskById(taskId) ?: return@launch
            
            val selectedDays = try {
                task.repeatDays.split(",").map { it.trim().toInt() }.toSet()
            } catch (e: Exception) {
                emptySet()
            }
            
            _uiState.update {
                it.copy(
                    title = task.title,
                    hour = task.hour,
                    minute = task.minute,
                    selectedDays = selectedDays,
                    note = task.note ?: ""
                )
            }
        }
    }
    
    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title, titleError = null) }
    }
    
    fun updateTime(hour: Int, minute: Int) {
        _uiState.update { it.copy(hour = hour, minute = minute) }
    }
    
    fun toggleDay(day: Int) {
        _uiState.update {
            val newDays = if (day in it.selectedDays) {
                it.selectedDays - day
            } else {
                it.selectedDays + day
            }
            it.copy(selectedDays = newDays, daysError = null)
        }
    }
    
    fun updateNote(note: String) {
        _uiState.update { it.copy(note = note) }
    }
    
    fun saveTask(context: Context, taskId: Long?): Boolean {
        val state = _uiState.value
        
        // 유효성 검사
        var hasError = false
        
        if (state.title.isBlank()) {
            _uiState.update { it.copy(titleError = "제목을 입력하세요") }
            hasError = true
        }
        
        if (state.selectedDays.isEmpty()) {
            _uiState.update { it.copy(daysError = "최소 1개의 요일을 선택하세요") }
            hasError = true
        }
        
        if (hasError) return false
        
        // 저장
        viewModelScope.launch {
            val repeatDays = state.selectedDays.sorted().joinToString(",")
            
            if (taskId == null) {
                // 새로 추가
                val task = TaskEntity(
                    title = state.title.trim(),
                    enabled = true,
                    hour = state.hour,
                    minute = state.minute,
                    repeatDays = repeatDays,
                    note = state.note.trim().ifBlank { null }
                )
                val newId = repository.insertTask(task)
                
                // 알람 등록
                val scheduler = AlarmScheduler(context)
                scheduler.scheduleAlarm(task.copy(id = newId))
            } else {
                // 수정
                val existingTask = repository.getTaskById(taskId) ?: return@launch
                val updatedTask = existingTask.copy(
                    title = state.title.trim(),
                    hour = state.hour,
                    minute = state.minute,
                    repeatDays = repeatDays,
                    note = state.note.trim().ifBlank { null },
                    updatedAt = System.currentTimeMillis()
                )
                repository.updateTask(updatedTask)
                
                // 알람 재등록 (enabled인 경우만)
                if (updatedTask.enabled) {
                    val scheduler = AlarmScheduler(context)
                    scheduler.cancelAlarm(taskId)
                    scheduler.scheduleAlarm(updatedTask)
                }
            }
        }
        
        return true
    }
    
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ForgetLessApplication)
                TaskEditViewModel(application.repository)
            }
        }
    }
}

