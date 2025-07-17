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
import com.example.ui.theme.AppTypography

val LightColorScheme = lightColorScheme(
    // Primary (Yellow for actions)
    primary = Color(0xFFE6A800), // Rich amber-yellow, better on white
    onPrimary = Color(0xFFFFFFFF), // Still white text on buttons etc.
    primaryContainer = Color(0xFFFFE08D), // Softer container
    onPrimaryContainer = Color(0xFF3E2C00) // Dark text on container
    , // Deep brown for contrast

    // Secondary (Blue for information)
    secondary = Color(0xFF2196F3),            // Same clear blue
    onSecondary = Color(0xFFFFFFFF),          // White on blue
    secondaryContainer = Color(0xFFBBDEFB),   // Light blue container
    onSecondaryContainer = Color(0xFF002B4F), // Deep blue text/icon

    // Tertiary (Green for success)
    tertiary = Color(0xFF4CAF50),             // Same fresh green
    onTertiary = Color(0xFFFFFFFF),           // White on green
    tertiaryContainer = Color(0xFFC8F5C8),    // Light green container
    onTertiaryContainer = Color(0xFF003813),  // Dark green text/icon

    // Error (Red for errors)
    error = Color(0xFFF44336),                // Vivid red
    onError = Color(0xFFFFFFFF),              // White text/icon
    errorContainer = Color(0xFFFFDAD6),       // Light red background
    onErrorContainer = Color(0xFF630005),     // Deep red text/icon

    // Background & Surfaces
    background = Color(0xFFFFFFFF),           // Pure white background
    onBackground = Color(0xFF1A1A1A),         // Deep grey/black text

    surface = Color(0xFFF9F9F9),              // Slight off-white for surface
    onSurface = Color(0xFF1A1A1A),            // Dark grey text on surface

    // Surface Variants & Outline
    surfaceVariant = Color(0xFFE0E0E0),       // Light neutral grey for cards
    onSurfaceVariant = Color(0xFF404040),     // Darker grey text/icon
    outline = Color(0xFF757575)               // Same subtle grey for dividers
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
        typography = AppTypography,
        content = content
    )
}