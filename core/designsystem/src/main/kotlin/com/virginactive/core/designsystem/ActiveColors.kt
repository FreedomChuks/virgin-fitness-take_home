package com.virginactive.core.designsystem

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object ActiveBrandColors {
    val Red = Color(0xFFC90035)
    val RedDeep = Color(0xFF8A0024)
    val OnRed = Color.White
}

internal val LightColors =
    lightColorScheme(
        primary = ActiveBrandColors.Red,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFFFDADF),
        onPrimaryContainer = Color(0xFF40000E),
        inversePrimary = Color(0xFFFFB2BE),
        secondary = Color(0xFF5B5355),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFECE4E3),
        onSecondaryContainer = Color(0xFF211A1B),
        tertiary = Color(0xFF26405C),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFE6ECF4),
        onTertiaryContainer = Color(0xFF0D1D2E),
        background = Color(0xFFFBF9F8),
        onBackground = Color(0xFF1C1718),
        surface = Color(0xFFFBF9F8),
        onSurface = Color(0xFF1C1718),
        surfaceVariant = Color(0xFFEDE6E5),
        onSurfaceVariant = Color(0xFF574E50),
        surfaceTint = ActiveBrandColors.Red,
        surfaceBright = Color(0xFFFBF9F8),
        surfaceDim = Color(0xFFDDD8D7),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF6F3F2),
        surfaceContainer = Color(0xFFF1EDEC),
        surfaceContainerHigh = Color(0xFFEAE5E4),
        surfaceContainerHighest = Color(0xFFE4DEDD),
        inverseSurface = Color(0xFF322E2F),
        inverseOnSurface = Color(0xFFF7EFEF),
        outline = Color(0xFF857B7D),
        outlineVariant = Color(0xFFE0D8D8),
        error = Color(0xFFBA1A1A),
        onError = Color.White,
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF7A0008),
        scrim = Color.Black,
    )

internal val DarkColors =
    darkColorScheme(
        primary = Color(0xFFFF4A63),
        onPrimary = Color(0xFF2B0008),
        primaryContainer = Color(0xFF5C0019),
        onPrimaryContainer = Color(0xFFFFD9DE),
        inversePrimary = ActiveBrandColors.Red,
        secondary = Color(0xFFD5C2C4),
        onSecondary = Color(0xFF3A2D2F),
        secondaryContainer = Color(0xFF3F3537),
        onSecondaryContainer = Color(0xFFF2DEE0),
        tertiary = Color(0xFFC6D8EE),
        onTertiary = Color(0xFF15283B),
        tertiaryContainer = Color(0xFF1F2E40),
        onTertiaryContainer = Color(0xFFE2EDFA),
        background = Color(0xFF121011),
        onBackground = Color(0xFFECE0E1),
        surface = Color(0xFF121011),
        onSurface = Color(0xFFECE0E1),
        surfaceVariant = Color(0xFF3A3335),
        onSurfaceVariant = Color(0xFFD2C3C5),
        surfaceTint = Color(0xFFFF4A63),
        surfaceBright = Color(0xFF393435),
        surfaceDim = Color(0xFF121011),
        surfaceContainerLowest = Color(0xFF1A1718),
        surfaceContainerLow = Color(0xFF1C191A),
        surfaceContainer = Color(0xFF221E1F),
        surfaceContainerHigh = Color(0xFF2B2728),
        surfaceContainerHighest = Color(0xFF363132),
        inverseSurface = Color(0xFFECE0E1),
        inverseOnSurface = Color(0xFF322E2F),
        outline = Color(0xFF9B8D8F),
        outlineVariant = Color(0xFF3F3739),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF5C0B0B),
        onErrorContainer = Color(0xFFFFDAD6),
        scrim = Color.Black,
    )
