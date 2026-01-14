package com.geniusbrain.forgetless.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.geniusbrain.forgetless.data.TaskEntity
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    companion object {
        private const val TAG = "AlarmScheduler"
        const val SNOOZE_OFFSET = 1_000_000
    }
    
    fun scheduleAlarm(task: TaskEntity) {
        if (!task.enabled) {
            Log.d(TAG, "Task ${task.id} is disabled, skipping schedule")
            return
        }
        
        val nextAlarmTime = calculateNextAlarmTime(task)
        if (nextAlarmTime == null) {
            Log.w(TAG, "Could not calculate next alarm time for task ${task.id}")
            return
        }
        
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("TASK_ID", task.id)
            putExtra("TASK_TITLE", task.title)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        nextAlarmTime,
                        pendingIntent
                    )
                    Log.d(TAG, "Scheduled exact alarm for task ${task.id} at $nextAlarmTime")
                } else {
                    Log.w(TAG, "Cannot schedule exact alarms, permission needed")
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    nextAlarmTime,
                    pendingIntent
                )
                Log.d(TAG, "Scheduled exact alarm for task ${task.id} at $nextAlarmTime")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule alarm for task ${task.id}", e)
        }
    }
    
    fun cancelAlarm(taskId: Long) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
        Log.d(TAG, "Cancelled alarm for task $taskId")
    }
    
    fun scheduleSnooze(taskId: Long, taskTitle: String) {
        val snoozeTime = System.currentTimeMillis() + 10 * 60 * 1000 // 10분 후
        
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("TASK_ID", taskId)
            putExtra("TASK_TITLE", taskTitle)
            putExtra("IS_SNOOZE", true)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (taskId + SNOOZE_OFFSET).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        snoozeTime,
                        pendingIntent
                    )
                    Log.d(TAG, "Scheduled snooze alarm for task $taskId at $snoozeTime")
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    snoozeTime,
                    pendingIntent
                )
                Log.d(TAG, "Scheduled snooze alarm for task $taskId at $snoozeTime")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule snooze for task $taskId", e)
        }
    }
    
    fun cancelSnooze(taskId: Long) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (taskId + SNOOZE_OFFSET).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
        Log.d(TAG, "Cancelled snooze for task $taskId")
    }
    
    private fun calculateNextAlarmTime(task: TaskEntity): Long? {
        val repeatDays = parseRepeatDays(task.repeatDays)
        if (repeatDays.isEmpty()) return null
        
        val now = LocalDateTime.now()
        val taskTime = LocalTime.of(task.hour, task.minute)
        
        // 오늘부터 최대 14일 후까지 탐색
        for (daysOffset in 0..14) {
            val candidateDate = LocalDate.now().plusDays(daysOffset.toLong())
            val dayOfWeek = candidateDate.dayOfWeek.value // 월=1, 일=7
            
            if (dayOfWeek in repeatDays) {
                val candidateDateTime = LocalDateTime.of(candidateDate, taskTime)
                
                // 이미 지난 시간이면 스킵
                if (candidateDateTime.isAfter(now)) {
                    return candidateDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                }
            }
        }
        
        return null
    }
    
    private fun parseRepeatDays(repeatDaysStr: String): Set<Int> {
        return try {
            repeatDaysStr.split(",")
                .map { it.trim().toInt() }
                .filter { it in 1..7 }
                .toSet()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse repeat days: $repeatDaysStr", e)
            emptySet()
        }
    }
}

