package com.geniusbrain.forgetless.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.geniusbrain.forgetless.ui.components.StatusBadge
import com.geniusbrain.forgetless.ui.components.TaskCard
import com.geniusbrain.forgetless.ui.components.TaskStatus
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToTaskList: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        val today = LocalDate.now()
                        val dayOfWeek = today.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
                        Text(
                            text = "${today.monthValue}월 ${today.dayOfMonth}일 $dayOfWeek",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "오늘 할 것 ${uiState.todayTasks.size}개",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Button(
                onClick = onNavigateToTaskList,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Text("전체 항목", style = MaterialTheme.typography.labelLarge)
            }
        }
    ) { padding ->
        if (uiState.todayTasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "오늘은 할 일이 없어요!",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(uiState.todayTasks, key = { it.task.id }) { taskWithStatus ->
                    val task = taskWithStatus.task
                    val status = taskWithStatus.status
                    
                    // 상태 결정 (완료 > 봤음 > 안 봄)
                    val taskStatus = when {
                        status?.completedAt != null -> TaskStatus.COMPLETED
                        status?.seenAt != null -> TaskStatus.SEEN
                        else -> TaskStatus.NOT_SEEN
                    }
                    
                    TaskCard(
                        task = task,
                        statusBadge = { StatusBadge(status = taskStatus) },
                        onToggleEnabled = { enabled ->
                            viewModel.toggleTaskEnabled(context, task.id, enabled)
                        },
                        onComplete = {
                            viewModel.completeTask(task.id)
                        }
                    )
                }
            }
        }
    }
}

