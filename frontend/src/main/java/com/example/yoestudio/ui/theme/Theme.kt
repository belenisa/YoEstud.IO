package com.example.yoestudio.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

val LocalThemeManager = staticCompositionLocalOf {
    mutableStateOf(true)
}

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkOnSurface,
    onBackground = DarkOnSurface,
    onSurface = DarkOnSurface,
    secondary = DarkSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = LightOnSurface,
    onBackground = LightOnSurface,
    onSurface = LightOnSurface,
    secondary = LightSecondary
)

@Composable
fun YoEstudioTheme(
    themeState: MutableState<Boolean>,
    content: @Composable () -> Unit
) {
    val isDark = themeState.value

    val primary by animateColorAsState(
        targetValue = if (isDark) DarkPrimary else LightPrimary,
        animationSpec = tween(400)
    )
    val background by animateColorAsState(
        targetValue = if (isDark) DarkBackground else LightBackground,
        animationSpec = tween(400)
    )
    val surface by animateColorAsState(
        targetValue = if (isDark) DarkSurface else LightSurface,
        animationSpec = tween(400)
    )
    val onSurface by animateColorAsState(
        targetValue = if (isDark) DarkOnSurface else LightOnSurface,
        animationSpec = tween(400)
    )
    val secondary by animateColorAsState(
        targetValue = if (isDark) DarkSecondary else LightSecondary,
        animationSpec = tween(400)
    )

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = primary,
            background = background,
            surface = surface,
            onPrimary = onSurface,
            onBackground = onSurface,
            onSurface = onSurface,
            secondary = secondary
        )
    } else {
        lightColorScheme(
            primary = primary,
            background = background,
            surface = surface,
            onPrimary = onSurface,
            onBackground = onSurface,
            onSurface = onSurface,
            secondary = secondary
        )
    }

    CompositionLocalProvider(LocalThemeManager provides themeState) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
