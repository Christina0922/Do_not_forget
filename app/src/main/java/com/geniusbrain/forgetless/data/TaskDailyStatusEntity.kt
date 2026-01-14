package com.geniusbrain.forgetless.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "task_daily_status",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["taskId", "dateKey"], unique = true)]
)
data class TaskDailyStatusEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: Long,
    val dateKey: String, // "yyyy-MM-dd"
    val seenAt: Long? = null,
    val completedAt: Long? = null,
    val snoozedAt: Long? = null
)

