package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RudraXDarkColorScheme = darkColorScheme(
    primary = CyanNeon,
    onPrimary = PitchBlack,
    primaryContainer = CobaltBlue,
    onPrimaryContainer = TextPrimary,
    secondary = ElectricBlue,
    onSecondary = TextPrimary,
    secondaryContainer = CyberPurple,
    onSecondaryContainer = TextPrimary,
    tertiary = CyberPurple,
    onTertiary = TextPrimary,
    background = PitchBlack,
    onBackground = TextPrimary,
    surface = DeepVoid,
    onSurface = TextPrimary,
    surfaceVariant = GlassSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder,
    outlineVariant = GlassBorderMuted
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Mobile-first pitch black theme as specified by RUDRAX WiFi Vision 3D
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RudraXDarkColorScheme,
        typography = Typography,
        content = content
    )
}
