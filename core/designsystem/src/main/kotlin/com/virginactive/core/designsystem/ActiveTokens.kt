package com.virginactive.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

internal val ActiveShapes =
    Shapes(
        extraSmall = RoundedCornerShape(6.dp),
        small = RoundedCornerShape(10.dp),
        medium = RoundedCornerShape(14.dp),
        large = RoundedCornerShape(20.dp),
        extraLarge = RoundedCornerShape(28.dp),
    )

object ActiveSpace {
    val xxs = 2.dp
    val xs = 4.dp
    val small = 8.dp
    val smallMedium = 12.dp
    val medium = 16.dp
    val large = 24.dp
    val extraLarge = 32.dp

    val screen = 16.dp
}

object ActiveSize {
    val minTouch = 48.dp
    val button = 56.dp
    val iconSmall = 16.dp
    val icon = 20.dp
    val iconLarge = 24.dp
}

private fun style(size: Int, line: Int, weight: FontWeight, tracking: Double = 0.0) =
    TextStyle(
        fontSize = size.sp,
        lineHeight = line.sp,
        fontWeight = weight,
        letterSpacing = tracking.em,
    )

internal val ActiveTypography =
    Typography(
        displaySmall = style(34, 40, FontWeight.Bold, -0.02),
        headlineLarge = style(28, 34, FontWeight.Bold, -0.02),
        headlineMedium = style(24, 30, FontWeight.Bold, -0.015),
        headlineSmall = style(21, 27, FontWeight.Bold, -0.01),
        titleLarge = style(19, 25, FontWeight.SemiBold, -0.005),
        titleMedium = style(16, 22, FontWeight.SemiBold),
        titleSmall = style(15, 20, FontWeight.SemiBold),
        bodyLarge = style(16, 24, FontWeight.Normal),
        bodyMedium = style(14, 20, FontWeight.Normal),
        bodySmall = style(12, 16, FontWeight.Normal, 0.01),
        labelLarge = style(14, 20, FontWeight.SemiBold, 0.005),
        labelMedium = style(12, 16, FontWeight.SemiBold, 0.03),
        labelSmall = style(11, 14, FontWeight.Medium, 0.04),
    )
