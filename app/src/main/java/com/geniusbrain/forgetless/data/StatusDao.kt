package com.geniusbrain.forgetless.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StatusDao {
    @Query("SELECT * FROM task_daily_status WHERE taskId = :taskId AND dateKey = :dateKey")
    suspend fun getStatus(taskId: Long, dateKey: String): TaskDailyStatusEntity?
    
    @Query("SELECT * FROM task_daily_status WHERE taskId = :taskId AND dateKey = :dateKey")
    fun getStatusFlow(taskId: Long, dateKey: String): Flow<TaskDailyStatusEntity?>
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStatus(status: TaskDailyStatusEntity): Long
    
    @Update
    suspend fun updateStatus(status: TaskDailyStatusEntity)
    
    @Transaction
    suspend fun upsertStatus(status: TaskDailyStatusEntity) {
        val existing = getStatus(status.taskId, status.dateKey)
        if (existing == null) {
            insertStatus(status)
        } else {
            updateStatus(status.copy(id = existing.id))
        }
    }
    
    @Query("""
        UPDATE task_daily_status 
        SET seenAt = :seenAt 
        WHERE taskId = :taskId AND dateKey = :dateKey AND seenAt IS NULL
    """)
    suspend fun markAsSeen(taskId: Long, dateKey: String, seenAt: Long)
    
    @Query("""
        UPDATE task_daily_status 
        SET completedAt = :completedAt 
        WHERE taskId = :taskId AND dateKey = :dateKey
    """)
    suspend fun markAsCompleted(taskId: Long, dateKey: String, completedAt: Long)
    
    @Query("""
        UPDATE task_daily_status 
        SET snoozedAt = :snoozedAt 
        WHERE taskId = :taskId AND dateKey = :dateKey
    """)
    suspend fun markAsSnoozed(taskId: Long, dateKey: String, snoozedAt: Long)
}

