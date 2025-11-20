package com.markoala.tomoandroid.ui.theme

import androidx.compose.ui.graphics.Color

object CustomColor {
    // Brand colors
    val primary = Color(0xFFA17353)
    val primaryDim = Color(0xFF80583F)
    val primaryContainer = Color(0xFFF4E6DB)
    val secondary = Color(0xFFFF8465)
    val secondaryContainer = Color(0xFFFFDAD0)

    // Semantic colors
    val success = Color(0xFF3AC566)
    val warning = Color(0xFFF6C145)
    val danger = Color(0xFFF35B55)
    val error = danger

    // Neutral palette (Toss inspired)
    val gray900 = Color(0xFF1F1F1F)
    val gray700 = Color(0xFF4E4E4E)
    val gray500 = Color(0xFF8C8C8C)
    val gray300 = Color(0xFFD9D9D9)
    val gray200 = Color(0xFFE5E8EB)
    val gray100 = Color(0xFFF2F4F6)
    val gray50 = Color(0xFFF7F9FB)

    val white = Color(0xFFFFFFFF)
    val black = gray900
    val charcoal = gray700
    val deepBrown = Color(0xFF4A3F36)

    // Themed helpers
    val background = gray50
    val surface = gray100
    val outline = gray200
    val divider = gray300
    val textPrimary = gray900
    val textBody = gray700
    val textSecondary = gray500

    // Legacy aliases kept for backwards compatibility while refactoring
    val gray30 = gray50
    val gray40 = gray100
    val gray50Legacy = gray200

    // Deprecated names used by existing UI components
    val pastelRed = Color(0xFFFFB3BA)
    val lightRed = Color(0xFFFFC1C1)
    val darkRed = Color(0xFFB91C1C)
    val redText = Color(0xFFDC2626)
}
