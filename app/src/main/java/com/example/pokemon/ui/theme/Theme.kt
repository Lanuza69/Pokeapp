package com.example.pokemon.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = RedPrimary,
    onPrimary = WhiteOnBlack,
    primaryContainer = RedSecondary,
    background = BlackBackground,
    surface = DarkSurface,
    onSurface = WhiteOnBlack
)

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = WhiteBackground,
    primaryContainer = BlueSecondary,
    background = WhiteBackground,
    surface = BlueSurface,
    onSurface = TextOnWhite

    /* Otros colores por defecto que puedes sobrescribir:
    secondary = BlueSecondary,
    tertiary = BlueSecondary
    */
)

@Composable
fun PokemonTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    // Esto maneja el color de la Barra de Estado (StatusBar) como hacías en el XML
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Se define en Type.kt
        content = content
    )
}