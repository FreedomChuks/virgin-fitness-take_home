package com.virginactive.features.timetable

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.virginactive.core.designsystem.ActiveScreenPreviews
import com.virginactive.core.designsystem.ActiveTheme
import com.virginactive.core.ui.ScreenState
import com.virginactive.core.ui.preview.PreviewData
import java.time.LocalDate

@ActiveScreenPreviews
@Composable
private fun TimetableDayPreview() {
    ActiveTheme {
        Surface {
            TimetableScreen(
                TimetableState(ScreenState.Ready(PreviewData.table), LocalDate.parse("2035-05-15")),
                {})
        }
    }
}

@ActiveScreenPreviews
@Composable
private fun TimetableWeekPreview() {
    ActiveTheme {
        Surface { TimetableScreen(TimetableState(ScreenState.Ready(PreviewData.table), null), {}) }
    }
}

@ActiveScreenPreviews
@Composable
private fun EmptyDayPreview() {
    ActiveTheme {
        Surface {
            TimetableScreen(
                TimetableState(ScreenState.Ready(PreviewData.table), LocalDate.parse("2035-05-16")),
                {})
        }
    }
}

@ActiveScreenPreviews
@Composable
private fun TimetableRefreshingPreview() {
    ActiveTheme {
        Surface {
            TimetableScreen(
                TimetableState(
                    ScreenState.Ready(PreviewData.table, refreshing = true),
                    LocalDate.parse("2035-05-15")),
                {})
        }
    }
}

@ActiveScreenPreviews
@Composable
private fun TimetableRefreshFailedPreview() {
    ActiveTheme {
        Surface {
            TimetableScreen(
                TimetableState(
                    ScreenState.Ready(
                        PreviewData.table, refreshError = "The server is busy. Try again."),
                    LocalDate.parse("2035-05-15")),
                {})
        }
    }
}
