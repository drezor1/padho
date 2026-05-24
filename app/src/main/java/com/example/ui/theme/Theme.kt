package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SecondaryBlue,
    onPrimary = DarkBg,
    primaryContainer = PrimaryBlue,
    onPrimaryContainer = DarkOnBg,
    secondary = AccentTeal,
    onSecondary = DarkBg,
    tertiary = AccentAmber,
    onTertiary = DarkBg,
    background = DarkBg,
    onBackground = DarkOnBg,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = DarkOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = LightSurface,
    primaryContainer = SecondaryBlue,
    onPrimaryContainer = LightOnBg,
    secondary = AccentTeal,
    onSecondary = LightSurface,
    tertiary = AccentAmber,
    onTertiary = LightOnBg,
    background = LightBg,
    onBackground = LightOnBg,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightBg,
    onSurfaceVariant = LightOnSurface
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
