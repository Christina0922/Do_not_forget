package com.geniusbrain.forgetless.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = LimeAccent,
    secondary = LimeDim,
    tertiary = LimeDim,
    background = DarkNavy,
    surface = CardBackground,
    onPrimary = DarkNavy,
    onSecondary = DarkNavy,
    onTertiary = DarkNavy,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = ErrorRed
)

@Composable
fun ForgetLessTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

