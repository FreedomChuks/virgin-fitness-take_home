package com.virginactive.core.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class ActiveStatusTone {
    Success,
    Waitlist,
    Unavailable,
    Error,
    Info,
}

@Immutable data class ActiveStatusColors(val container: Color, val content: Color)

@Immutable
data class ActiveStatusPalette(
    val success: ActiveStatusColors,
    val waitlist: ActiveStatusColors,
    val unavailable: ActiveStatusColors,
    val error: ActiveStatusColors,
    val info: ActiveStatusColors,
) {
    fun of(tone: ActiveStatusTone): ActiveStatusColors =
        when (tone) {
            ActiveStatusTone.Success -> success
            ActiveStatusTone.Waitlist -> waitlist
            ActiveStatusTone.Unavailable -> unavailable
            ActiveStatusTone.Error -> error
            ActiveStatusTone.Info -> info
        }
}

internal val LightStatusPalette =
    ActiveStatusPalette(
        success = ActiveStatusColors(Color(0xFFDCF2E3), Color(0xFF0E5A30)),
        waitlist = ActiveStatusColors(Color(0xFFFFEDC7), Color(0xFF684400)),
        unavailable = ActiveStatusColors(Color(0xFFECE7E6), Color(0xFF554D4F)),
        error = ActiveStatusColors(Color(0xFFFFDAD6), Color(0xFF7A0008)),
        info = ActiveStatusColors(Color(0xFFE6ECF4), Color(0xFF26405C)),
    )

internal val DarkStatusPalette =
    ActiveStatusPalette(
        success = ActiveStatusColors(Color(0xFF173A24), Color(0xFFA9E2BB)),
        waitlist = ActiveStatusColors(Color(0xFF433000), Color(0xFFFFD88C)),
        unavailable = ActiveStatusColors(Color(0xFF2D292A), Color(0xFFCEC4C6)),
        error = ActiveStatusColors(Color(0xFF5C0B0B), Color(0xFFFFDAD6)),
        info = ActiveStatusColors(Color(0xFF1F2E40), Color(0xFFC6D8EE)),
    )

internal val LocalActiveStatusPalette = staticCompositionLocalOf { LightStatusPalette }
