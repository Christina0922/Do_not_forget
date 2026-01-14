package com.geniusbrain.forgetless.alarm

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.geniusbrain.forgetless.data.AppDatabase
import com.geniusbrain.forgetless.data.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SnoozeActionReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "SnoozeActionReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra("TASK_ID", -1L)
        val taskTitle = intent.getStringExtra("TASK_TITLE") ?: "알림"
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", -1)
        
        if (taskId == -1L) {
            Log.e(TAG, "Invalid task ID received")
            return
        }
        
        Log.d(TAG, "Snooze action received for task $taskId")
        
        // 스누즈 알람 예약
        val scheduler = AlarmScheduler(context)
        scheduler.scheduleSnooze(taskId, taskTitle)
        
        // 현재 알림 닫기
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
        
        // 스누즈 기록 (선택)
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(context)
                val repository = Repository(database.taskDao(), database.statusDao())
                repository.markAsSnoozed(taskId)
                Log.d(TAG, "Marked task $taskId as snoozed")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to mark task $taskId as snoozed", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}

