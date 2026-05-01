package com.example.expensetracker.ui.theme

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
    primary = PrimaryRed,
    secondary = Taupe,
    tertiary = SecondaryRed,
    background = DeepBlack,
    surface = DeepBlack,
    onPrimary = White,
    onSecondary = DeepBlack,
    onTertiary = White,
    onBackground = White,
    onSurface = White,
    surfaceVariant = Color(0xFF1E1E1E),
    onSurfaceVariant = LightGray
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryRed,
    secondary = Taupe,
    tertiary = SecondaryRed,
    background = BackgroundBeige,
    surface = White,
    onPrimary = White,
    onSecondary = DeepBlack,
    onTertiary = White,
    onBackground = DeepBlack,
    onSurface = DeepBlack,
    surfaceVariant = White,
    onSurfaceVariant = TextGray
)

@Composable
fun ExpenseTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set to false by default to maintain the brand color palette (Red/Beige/Black)
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