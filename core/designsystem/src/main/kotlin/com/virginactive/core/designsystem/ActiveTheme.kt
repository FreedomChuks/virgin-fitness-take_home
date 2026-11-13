package com.virginactive.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

@Composable
fun ActiveTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalActiveStatusPalette provides
            if (darkTheme) DarkStatusPalette else LightStatusPalette) {
            MaterialTheme(
                colorScheme = if (darkTheme) DarkColors else LightColors,
                typography = ActiveTypography,
                shapes = ActiveShapes,
                content = content,
            )
        }
}

object ActiveTheme {
    val status: ActiveStatusPalette
        @Composable @ReadOnlyComposable get() = LocalActiveStatusPalette.current
}
