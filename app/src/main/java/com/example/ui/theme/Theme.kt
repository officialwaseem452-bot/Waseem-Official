package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

val ZeroShotColorScheme = darkColorScheme(
    primary = ZeroShotPrimary,
    onPrimary = ZeroShotOnPrimary,
    primaryContainer = ZeroShotPrimaryContainer,
    onPrimaryContainer = ZeroShotPrimaryVariant,
    secondary = ZeroShotSecondary,
    onSecondary = ZeroShotOnSecondary,
    secondaryContainer = ZeroShotSecondaryContainer,
    onSecondaryContainer = ZeroShotSecondary,
    tertiary = ZeroShotTertiary,
    tertiaryContainer = ZeroShotTertiaryContainer,
    background = ZeroShotBackground,
    onBackground = ZeroShotOnBackground,
    surface = ZeroShotSurface,
    onSurface = ZeroShotOnSurface,
    surfaceVariant = ZeroShotSurfaceVariant,
    onSurfaceVariant = ZeroShotOnSurfaceVariant,
    outline = ZeroShotOutline,
    outlineVariant = ZeroShotOutlineVariant,
    error = ZeroShotError
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = ZeroShotColorScheme,
        typography = Typography,
        content = content
    )
}

