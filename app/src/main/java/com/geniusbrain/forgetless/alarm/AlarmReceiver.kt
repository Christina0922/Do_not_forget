package com.geniusbrain.forgetless.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.geniusbrain.forgetless.data.AppDatabase
import com.geniusbrain.forgetless.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "AlarmReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra("TASK_ID", -1L)
        val taskTitle = intent.getStringExtra("TASK_TITLE") ?: "알림"
        val isSnooze = intent.getBooleanExtra("IS_SNOOZE", false)
        
        if (taskId == -1L) {
            Log.e(TAG, "Invalid task ID received")
            return
        }
        
        Log.d(TAG, "Alarm received for task $taskId (snooze: $isSnooze)")
        
        // 알림 표시
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showTaskNotification(taskId, taskTitle)
        
        // 스누즈가 아닌 경우에만 다음 알람 재스케줄
        if (!isSnooze) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val database = AppDatabase.getDatabase(context)
                    val task = database.taskDao().getTaskById(taskId)
                    
                    if (task != null && task.enabled) {
                        val scheduler = AlarmScheduler(context)
                        scheduler.scheduleAlarm(task)
                        Log.d(TAG, "Rescheduled next alarm for task $taskId")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to reschedule alarm for task $taskId", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}

