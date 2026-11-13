package com.virginactive.features.home

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.virginactive.core.designsystem.ActiveScreenPreviews
import com.virginactive.core.designsystem.ActiveTheme
import com.virginactive.core.ui.ScreenState
import com.virginactive.core.ui.preview.PreviewData

@ActiveScreenPreviews
@Composable
private fun HomePreview() {
    ActiveTheme { Surface { HomeScreen(ScreenState.Ready(PreviewData.home), {}) } }
}

@ActiveScreenPreviews
@Composable
private fun HomeRefreshFailedPreview() {
    ActiveTheme {
        Surface {
            HomeScreen(
                ScreenState.Ready(
                    PreviewData.home, refreshError = "The server is busy. Please try again."),
                {})
        }
    }
}

@ActiveScreenPreviews
@Composable
private fun HomeEmptyPreview() {
    ActiveTheme { Surface { HomeScreen(ScreenState.Ready(emptyList()), {}) } }
}

@ActiveScreenPreviews
@Composable
private fun HomeLoadingPreview() {
    ActiveTheme { Surface { HomeScreen(ScreenState.Loading, {}) } }
}

@ActiveScreenPreviews
@Composable
private fun HomeErrorPreview() {
    ActiveTheme {
        Surface { HomeScreen(ScreenState.Failed("We couldn't connect.\nReference: req_1234"), {}) }
    }
}
