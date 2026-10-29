package com.smartkebaya.pattern.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KebayaLightColors = lightColorScheme(
    primary = Color(0xFF8B2C3B),
    secondary = Color(0xFFB5651D),
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE)
)

private val KebayaDarkColors = darkColorScheme(
    primary = Color(0xFFE0A6AE),
    secondary = Color(0xFFE0A96B),
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F)
)

@Composable
fun SmartKebayaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) KebayaDarkColors else KebayaLightColors
    MaterialTheme(colorScheme = colors, content = content)
}