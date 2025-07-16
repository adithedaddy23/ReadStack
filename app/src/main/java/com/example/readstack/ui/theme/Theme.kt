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
    // Primary (Yellow for actions)
    primary = Color(0xFFFFC107), // A vibrant, warm yellow
    onPrimary = Color(0xFF000000), // Black for high contrast on yellow
    primaryContainer = Color(0xFF4D3800), // A dark, muted yellow for containers
    onPrimaryContainer = Color(0xFFFFDFA6), // Light yellow for text/icons on the container

    // Secondary (Blue for information)
    secondary = Color(0xFF2196F3), // A clear, standard blue
    onSecondary = Color(0xFFFFFFFF), // White for high contrast on blue
    secondaryContainer = Color(0xFF003062), // A deep blue for containers
    onSecondaryContainer = Color(0xFFD0E4FF), // Light blue for text/icons on the container

    // Tertiary (Green for success)
    tertiary = Color(0xFF4CAF50), // A positive, clear green
    onTertiary = Color(0xFFFFFFFF), // White for high contrast on green
    tertiaryContainer = Color(0xFF003813), // A deep green for containers
    onTertiaryContainer = Color(0xFFC8F5C8), // Light green for text/icons on the container

    // Error (Red for errors)
    error = Color(0xFFF44336), // A strong, standard red
    onError = Color(0xFFFFFFFF), // White for high contrast on red
    errorContainer = Color(0xFF630005), // A deep red for containers
    onErrorContainer = Color(0xFFFFDAD6), // Light red for text/icons on the container

    // Background & Surfaces (Black/Dark Grey)
    background = Color(0xFF121212), // A deep, neutral dark grey (near black)
    onBackground = Color(0xFFE6E6E6), // Soft off-white for text and icons
    surface = Color(0xFF121212), // Surface color matches the background
    onSurface = Color(0xFFE6E6E6), // Text on surface color

    // Surface Variants & Outlines
    surfaceVariant = Color(0xFF424242), // A neutral grey for cards and elevated surfaces
    onSurfaceVariant = Color(0xFFBDBDBD), // Lighter grey for text on variant surfaces
    outline = Color(0xFF757575) // A subtle grey for borders and dividers
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