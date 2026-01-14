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

class CompleteActionReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "CompleteActionReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra("TASK_ID", -1L)
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", -1)
        
        if (taskId == -1L) {
            Log.e(TAG, "Invalid task ID received")
            return
        }
        
        Log.d(TAG, "Complete action received for task $taskId")
        
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(context)
                val repository = Repository(database.taskDao(), database.statusDao())
                
                // 오늘 날짜로 완료 처리
                repository.markAsCompleted(taskId)
                Log.d(TAG, "Marked task $taskId as completed")
                
                // 알림 닫기
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(notificationId)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to mark task $taskId as completed", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}

