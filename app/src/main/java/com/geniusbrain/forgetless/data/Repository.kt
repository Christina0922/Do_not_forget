package com.geniusbrain.forgetless.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class Repository(
    private val taskDao: TaskDao,
    private val statusDao: StatusDao
) {
    // Task operations
    fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()
    
    suspend fun getTaskById(taskId: Long): TaskEntity? = taskDao.getTaskById(taskId)
    
    suspend fun getEnabledTasks(): List<TaskEntity> = taskDao.getEnabledTasks()
    
    suspend fun insertTask(task: TaskEntity): Long = taskDao.insertTask(task)
    
    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)
    
    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)
    
    // Status operations
    suspend fun getStatus(taskId: Long, dateKey: String): TaskDailyStatusEntity? =
        statusDao.getStatus(taskId, dateKey)
    
    fun getStatusFlow(taskId: Long, dateKey: String): Flow<TaskDailyStatusEntity?> =
        statusDao.getStatusFlow(taskId, dateKey)
    
    suspend fun upsertStatus(status: TaskDailyStatusEntity) = statusDao.upsertStatus(status)
    
    suspend fun markAsSeen(taskId: Long, dateKey: String = getTodayDateKey()) {
        val existing = statusDao.getStatus(taskId, dateKey)
        if (existing == null) {
            statusDao.insertStatus(
                TaskDailyStatusEntity(
                    taskId = taskId,
                    dateKey = dateKey,
                    seenAt = System.currentTimeMillis()
                )
            )
        } else if (existing.seenAt == null) {
            statusDao.markAsSeen(taskId, dateKey, System.currentTimeMillis())
        }
    }
    
    suspend fun markAsCompleted(taskId: Long, dateKey: String = getTodayDateKey()) {
        val existing = statusDao.getStatus(taskId, dateKey)
        if (existing == null) {
            statusDao.insertStatus(
                TaskDailyStatusEntity(
                    taskId = taskId,
                    dateKey = dateKey,
                    completedAt = System.currentTimeMillis()
                )
            )
        } else {
            statusDao.markAsCompleted(taskId, dateKey, System.currentTimeMillis())
        }
    }
    
    suspend fun markAsSnoozed(taskId: Long, dateKey: String = getTodayDateKey()) {
        val existing = statusDao.getStatus(taskId, dateKey)
        if (existing == null) {
            statusDao.insertStatus(
                TaskDailyStatusEntity(
                    taskId = taskId,
                    dateKey = dateKey,
                    snoozedAt = System.currentTimeMillis()
                )
            )
        } else {
            statusDao.markAsSnoozed(taskId, dateKey, System.currentTimeMillis())
        }
    }
    
    companion object {
        fun getTodayDateKey(): String {
            return LocalDate.now().toString() // "yyyy-MM-dd"
        }
    }
}

