package com.example.yoestudio.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(

    primary = Color(0xFF1E2BFF),
    onPrimary = Color.White,

    background = Color(0xFF0B1A2B),
    onBackground = Color.White,

    surface = Color(0xFF13273C),
    onSurface = Color.White,

    secondary = Color(0xFF364AFF),
    onSecondary = Color.White
)


private val LightColorScheme = lightColorScheme(

    primary = Color(0xFF000000),
    onPrimary = Color.White,

    primaryContainer = Color(0xFF91D1F1),
    onPrimaryContainer = Color.White,

    background = Color(0xFF5DA4CE),
    onBackground = Color.Black,

    surface = Color(0xFF000000),
    onSurface = Color.White
)

@Composable
fun YoEstudioTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {

    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}