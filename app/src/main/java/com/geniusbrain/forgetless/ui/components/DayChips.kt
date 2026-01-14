package com.geniusbrain.forgetless.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DayChips(
    repeatDays: String,
    compact: Boolean = false,
    modifier: Modifier = Modifier
) {
    val days = listOf("월", "화", "수", "목", "금", "토", "일")
    val selectedDays = try {
        repeatDays.split(",").map { it.trim().toInt() }.filter { it in 1..7 }.toSet()
    } catch (e: Exception) {
        emptySet()
    }
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        days.forEachIndexed { index, day ->
            val dayNum = index + 1
            val isSelected = dayNum in selectedDays
            
            if (compact) {
                // 간단 표시 (선택된 요일만)
                if (isSelected) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = day,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            } else {
                // 전체 표시
                Surface(
                    color = if (isSelected) MaterialTheme.colorScheme.primary 
                           else MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = day,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary 
                               else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DayChipsSelector(
    selectedDays: Set<Int>,
    onDayToggle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val days = listOf("월", "화", "수", "목", "금", "토", "일")
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        days.forEachIndexed { index, day ->
            val dayNum = index + 1
            val isSelected = dayNum in selectedDays
            
            FilterChip(
                selected = isSelected,
                onClick = { onDayToggle(dayNum) },
                label = { Text(day) }
            )
        }
    }
}

