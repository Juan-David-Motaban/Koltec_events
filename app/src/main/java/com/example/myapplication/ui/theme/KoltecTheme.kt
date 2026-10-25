package com.example.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KoltecColorScheme = lightColorScheme(
    primary = Violeta,
    onPrimary = Color.White,
    primaryContainer = LavandaIcono,
    onPrimaryContainer = MoradoOscuro,
    secondary = Naranja,
    onSecondary = Color.White,
    secondaryContainer = NaranjaClaro,
    onSecondaryContainer = MoradoOscuro,
    tertiary = VerdeEstado,
    background = Lavanda,
    onBackground = MoradoOscuro,
    surface = Color.White,
    onSurface = MoradoOscuro,
    surfaceVariant = LavandaIcono,
    onSurfaceVariant = TextoSuave,
    outline = Color(0xFFB9AEE8),
    error = Rojo
)

/** Tema de Koltec Events (violeta y naranja). */
@Composable
fun KoltecEventsTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = KoltecColorScheme, content = content)
}
