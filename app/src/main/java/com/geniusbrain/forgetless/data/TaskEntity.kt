package com.geniusbrain.forgetless.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val enabled: Boolean = true,
    val hour: Int,
    val minute: Int,
    val repeatDays: String, // "1,2,3,4,5" (월=1 ~ 일=7)
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

