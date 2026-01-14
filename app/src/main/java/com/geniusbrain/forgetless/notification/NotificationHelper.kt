package com.geniusbrain.forgetless.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.geniusbrain.forgetless.MainActivity
// R 클래스는 빌드 시 자동 생성됨
import com.geniusbrain.forgetless.alarm.CompleteActionReceiver
import com.geniusbrain.forgetless.alarm.SnoozeActionReceiver

class NotificationHelper(private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    companion object {
        private const val CHANNEL_ID = "tasks"
        private const val CHANNEL_NAME = "할일 알림"
    }
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "숙제와 준비물을 알려줍니다"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun showTaskNotification(taskId: Long, taskTitle: String) {
        val notificationId = taskId.toInt()
        
        // 콘텐츠 인텐트: 알림 탭 시 앱 열기 + 봤음 처리
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("TASK_ID", taskId)
            putExtra("MARK_AS_SEEN", true)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // 완료 액션
        val completeIntent = Intent(context, CompleteActionReceiver::class.java).apply {
            putExtra("TASK_ID", taskId)
            putExtra("NOTIFICATION_ID", notificationId)
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId * 10 + 1,
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // 10분 뒤 액션
        val snoozeIntent = Intent(context, SnoozeActionReceiver::class.java).apply {
            putExtra("TASK_ID", taskId)
            putExtra("TASK_TITLE", taskTitle)
            putExtra("NOTIFICATION_ID", notificationId)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId * 10 + 2,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // 알림 빌드
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // 기본 아이콘 사용 (실제로는 앱 아이콘 필요)
            .setContentTitle("지금 할 시간!")
            .setContentText("$taskTitle - 완료하면 체크하세요")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(0, "완료", completePendingIntent)
            .addAction(0, "10분 뒤", snoozePendingIntent)
            .build()
        
        notificationManager.notify(notificationId, notification)
    }
}

