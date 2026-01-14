package com.geniusbrain.forgetless

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.geniusbrain.forgetless.data.AppDatabase
import com.geniusbrain.forgetless.data.Repository
import com.geniusbrain.forgetless.ui.HomeScreen
import com.geniusbrain.forgetless.ui.TaskEditScreen
import com.geniusbrain.forgetless.ui.TaskListScreen
import com.geniusbrain.forgetless.ui.theme.ForgetLessTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            // 권한 거부 시 처리 (선택적으로 설명 표시)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 알림 권한 요청 (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        
        // 정확 알람 권한 확인 (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as android.app.AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                // 설정 화면으로 이동하도록 안내 (선택적)
                // 여기서는 자동으로 설정 화면으로 이동하지 않고, 사용자가 필요시 처리하도록 함
            }
        }
        
        // 인텐트에서 봤음 처리 요청 확인
        handleNotificationIntent(intent)
        
        setContent {
            ForgetLessTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ForgetLessApp()
                }
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }
    
    private fun handleNotificationIntent(intent: Intent) {
        val taskId = intent.getLongExtra("TASK_ID", -1L)
        val markAsSeen = intent.getBooleanExtra("MARK_AS_SEEN", false)
        
        if (taskId != -1L && markAsSeen) {
            // 봤음 처리
            val repository = (application as ForgetLessApplication).repository
            lifecycleScope.launch {
                repository.markAsSeen(taskId)
            }
        }
    }
}

@Composable
fun ForgetLessApp() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToTaskList = {
                    navController.navigate("task_list")
                }
            )
        }
        
        composable("task_list") {
            TaskListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEdit = { taskId ->
                    val route = if (taskId != null) {
                        "task_edit/$taskId"
                    } else {
                        "task_edit/-1"
                    }
                    navController.navigate(route)
                }
            )
        }
        
        composable(
            route = "task_edit/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId")
            TaskEditScreen(
                taskId = if (taskId == -1L) null else taskId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

