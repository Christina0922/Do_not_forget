package com.geniusbrain.forgetless.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.geniusbrain.forgetless.ui.theme.ErrorRed
import com.geniusbrain.forgetless.ui.theme.LimeAccent
import com.geniusbrain.forgetless.ui.theme.TextSecondary

enum class TaskStatus {
    NOT_SEEN,    // 안 봄
    SEEN,        // 봤음
    COMPLETED    // 완료
}

@Composable
fun StatusBadge(
    status: TaskStatus,
    modifier: Modifier = Modifier
) {
    val (text, bgColor, textColor) = when (status) {
        TaskStatus.NOT_SEEN -> Triple("안 봄", ErrorRed.copy(alpha = 0.2f), ErrorRed)
        TaskStatus.SEEN -> Triple("봤음", TextSecondary.copy(alpha = 0.2f), TextSecondary)
        TaskStatus.COMPLETED -> Triple("완료", LimeAccent.copy(alpha = 0.2f), LimeAccent)
    }
    
    Surface(
        color = bgColor,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

