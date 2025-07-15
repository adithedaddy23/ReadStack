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

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3B5F64), // Slightly darker, richer teal for better contrast
    onPrimary = Color(0xFFFFFFFF), // White for max contrast
    primaryContainer = Color(0xFFC2E0E3), // Lighter, more distinct container
    onPrimaryContainer = Color(0xFF001F22), // Darker for readability

    secondary = Color(0xFF6A7D80), // Cooler, more distinct secondary
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD1E3E6), // Lighter, more neutral
    onSecondaryContainer = Color(0xFF1A2C2F), // Darker for contrast

    tertiary = Color(0xFFCB6848), // Brighter, warmer accent for distinction
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFD2C6), // Softer, distinct container
    onTertiaryContainer = Color(0xFF330B00), // Darker for readability

    error = Color(0xFFB00020), // Vivid red for errors
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFCDD2), // Softer error background
    onErrorContainer = Color(0xFF3C0008),

    background = Color(0xFFF7F9F9), // Slightly warmer neutral
    onBackground = Color(0xFF171A1A), // High contrast

    surface = Color(0xFFF7F9F9), // Matches background
    onSurface = Color(0xFF171A1A),
    surfaceVariant = Color(0xFFD5E2E4), // Cooler variant for cards
    onSurfaceVariant = Color(0xFF3A4647), // High contrast

    outline = Color(0xFF6A7778) // Subtle, distinct outline
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA3D5D8), // Lighter, more vibrant teal
    onPrimary = Color(0xFF002F33), // Darker for contrast
    primaryContainer = Color(0xFF2A474B), // Richer container
    onPrimaryContainer = Color(0xFFC2E0E3), // Matches light theme container

    secondary = Color(0xFFB8CED1), // Lighter, cooler secondary
    onSecondary = Color(0xFF1B2E31), // Darker for contrast
    secondaryContainer = Color(0xFF324447), // Distinct container
    onSecondaryContainer = Color(0xFFD1E3E6),

    tertiary = Color(0xFFE5A590), // Warmer, brighter accent
    onTertiary = Color(0xFF4F1708), // Darker for readability
    tertiaryContainer = Color(0xFF6E2C1A), // Richer container
    onTertiaryContainer = Color(0xFFFFD2C6),

    error = Color(0xFFF28B82), // Softer, distinct error
    onError = Color(0xFF5C0010),
    errorContainer = Color(0xFF8B0022),
    onErrorContainer = Color(0xFFFFCDD2),

    background = Color(0xFF141717), // Slightly warmer dark
    onBackground = Color(0xFFDEE0E0), // High contrast

    surface = Color(0xFF141717), // Matches background
    onSurface = Color(0xFFDEE0E0),
    surfaceVariant = Color(0xFF3A4647), // Matches light theme variant
    onSurfaceVariant = Color(0xFFB8C4C6), // High contrast

    outline = Color(0xFF849091) // Brighter, distinct outline
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