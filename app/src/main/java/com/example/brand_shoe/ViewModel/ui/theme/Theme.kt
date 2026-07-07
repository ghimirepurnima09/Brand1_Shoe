package com.example.brand_shoe.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = BrandOrange,
    onPrimary = Color.White,
    primaryContainer = BrandOrangeLight,
    onPrimaryContainer = BrandOrangeDark,
    secondary = Charcoal,
    onSecondary = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = BrandOrange,
    onPrimary = Color.White,
    primaryContainer = BrandOrangeDark,
    onPrimaryContainer = BrandOrangeLight,
    secondary = Color(0xFFE0E0E0),
    onSecondary = Charcoal,
    background = Color(0xFF121212),
    onBackground = Color(0xFFEDEDED),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFEDEDED),
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFB0B0B0),
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun Brand_ShoeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = BrandTypography,
        content = content
    )
}