
package com.example.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CyberColorScheme = lightColorScheme(
    primary = CyberOrange,
    onPrimary = Color.White,
    secondary = CyberViolet,
    onSecondary = Color.White,
    tertiary = CyberOrangeLight,
    onTertiary = CyberText,
    background = CyberBackground,
    onBackground = CyberText,
    surface = CyberSurface,
    onSurface = CyberText,
    surfaceVariant = CyberCard,
    onSurfaceVariant = CyberSecondaryText,
    outline = CyberBorder,
    error = CyberError,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CyberColorScheme,
        typography = Typography,
        content = content
    )
}
