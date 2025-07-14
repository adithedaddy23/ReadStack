package com.example.readstack.ui.theme

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

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4A6C6F),
    onPrimary = Color.White,
    secondary = Color(0xFF7D8C8F),
    background = Color(0xFFF9F9F9),
    surface = Color.White,
    error = Color(0xFFCC5C5C),
    onBackground = Color(0xFF1A1A1A),
    onSurface = Color(0xFF1A1A1A),
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF9ACFD0),
    onPrimary = Color.Black,
    secondary = Color(0xFFBFC9CC),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    error = Color(0xFFFF9999),
    onBackground = Color(0xFFEDEDED),
    onSurface = Color(0xFFEDEDED),
)


@Composable
fun ReadStackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}