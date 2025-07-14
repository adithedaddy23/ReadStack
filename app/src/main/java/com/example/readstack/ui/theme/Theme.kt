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
    primary = Color(0xFF4A6C6F),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD0E8EA),
    onPrimaryContainer = Color(0xFF002022),

    secondary = Color(0xFF7D8C8F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFDDE5E7),
    onSecondaryContainer = Color(0xFF192022),

    tertiary = Color(0xFFD37A5B), // Pronouncing accent color
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDBCF),
    onTertiaryContainer = Color(0xFF3A0800),

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = Color(0xFFF8FAFA),
    onBackground = Color(0xFF191C1C),

    surface = Color(0xFFF8FAFA),
    onSurface = Color(0xFF191C1C),
    surfaceVariant = Color(0xFFDAE4E5), // For cards, chips, etc.
    onSurfaceVariant = Color(0xFF3F4849),

    outline = Color(0xFF6F797A) // For borders and dividers
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF9ACFD0),
    onPrimary = Color(0xFF00373A),
    primaryContainer = Color(0xFF314F52),
    onPrimaryContainer = Color(0xFFD0E8EA),

    secondary = Color(0xFFBFC9CC),
    onSecondary = Color(0xFF1F3336),
    secondaryContainer = Color(0xFF36484B),
    onSecondaryContainer = Color(0xFFDDE5E7),

    tertiary = Color(0xFFE9B5A0), // Pronouncing accent color
    onTertiary = Color(0xFF5A1D0C),
    tertiaryContainer = Color(0xFF773320),
    onTertiaryContainer = Color(0xFFFFDBCF),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = Color(0xFF191C1C),
    onBackground = Color(0xFFE1E3E3),

    surface = Color(0xFF191C1C),
    onSurface = Color(0xFFE1E3E3),
    surfaceVariant = Color(0xFF3F4849), // For cards, chips, etc.
    onSurfaceVariant = Color(0xFFBFC8C9),

    outline = Color(0xFF899293) // For borders and dividers
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