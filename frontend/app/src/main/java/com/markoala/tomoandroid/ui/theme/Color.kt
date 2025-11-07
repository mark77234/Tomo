package com.markoala.tomoandroid.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val TomoLightColorScheme = lightColorScheme(
    primary = CustomColor.primary,
    onPrimary = CustomColor.white,
    primaryContainer = CustomColor.primaryContainer,
    onPrimaryContainer = CustomColor.primary,
    secondary = CustomColor.secondary,
    onSecondary = CustomColor.white,
    secondaryContainer = CustomColor.secondaryContainer,
    onSecondaryContainer = CustomColor.secondary,
    background = CustomColor.background,
    onBackground = CustomColor.textPrimary,
    surface = CustomColor.surface,
    onSurface = CustomColor.textBody,
    surfaceVariant = CustomColor.surface,
    onSurfaceVariant = CustomColor.textSecondary,
    outline = CustomColor.outline,
    inverseSurface = CustomColor.gray900,
    inverseOnSurface = CustomColor.gray50,
    error = CustomColor.danger,
    onError = CustomColor.white,
    errorContainer = CustomColor.danger.copy(alpha = 0.12f),
    scrim = Color(0x66000000)
)

val TomoDarkColorScheme = darkColorScheme(
    primary = CustomColor.primary,
    onPrimary = CustomColor.white,
    primaryContainer = CustomColor.primaryDim,
    onPrimaryContainer = CustomColor.white,
    secondary = CustomColor.secondary,
    onSecondary = CustomColor.white,
    background = Color(0xFF111216),
    onBackground = CustomColor.gray100,
    surface = Color(0xFF16171B),
    onSurface = CustomColor.gray100,
    surfaceVariant = Color(0xFF1F2024),
    onSurfaceVariant = CustomColor.gray500,
    outline = CustomColor.gray500,
    inverseSurface = CustomColor.gray50,
    inverseOnSurface = CustomColor.gray900,
    error = CustomColor.danger,
    onError = CustomColor.white,
    scrim = Color(0x99000000)
)

