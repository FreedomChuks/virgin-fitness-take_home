package com.virginactive.core.ui.preview

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.virginactive.core.designsystem.*
import com.virginactive.core.ui.*
import java.time.Instant

@ActivePreviews
@Composable
private fun ClassCardPreview() {
    ActiveTheme {
        Surface {
            Column(
                Modifier.padding(ActiveSpace.medium),
                verticalArrangement = Arrangement.spacedBy(ActiveSpace.small)) {
                    SectionTitle("Your week", actionLabel = "View all")
                    ClassCard(PreviewData.fitnessClass, Instant.now(), {})
                    ClassCard(PreviewData.bookedClass, Instant.now(), {})
                    ClassCard(PreviewData.fullClass, Instant.now(), {})
                    ClassCard(PreviewData.waitlistedClass, Instant.now(), {})
                }
        }
    }
}

@ActivePreviews
@Composable
private fun ClassCardEdgeCasesPreview() {
    ActiveTheme {
        Surface {
            Column(
                Modifier.padding(ActiveSpace.medium),
                verticalArrangement = Arrangement.spacedBy(ActiveSpace.small)) {
                    ClassCard(PreviewData.longTitleClass, Instant.now(), {})
                    ClassCard(PreviewData.finishedClass, Instant.now(), {})
                    ClassCard(PreviewData.cancelledClass, Instant.now(), {})
                }
        }
    }
}

@ActivePreviews
@Composable
private fun ErrorStatePreview() {
    ActiveTheme {
        Surface(Modifier.height(420.dp)) {
            StateContent<Unit>(
                ScreenState.Failed("We couldn't connect.\nReference: req_1234"), {}) {}
        }
    }
}

@ActivePreviews
@Composable
private fun LoadingStatePreview() {
    ActiveTheme {
        Surface(Modifier.height(240.dp)) { StateContent<Unit>(ScreenState.Loading, {}) {} }
    }
}

@ActivePreviews
@Composable
private fun RefreshFailedPreview() {
    ActiveTheme {
        Surface(Modifier.height(360.dp)) {
            RefreshableStateContent(
                ScreenState.Ready(
                    PreviewData.fitnessClass, refreshError = "The server is busy. Try again."),
                {}) {
                    LazyColumn(contentPadding = PaddingValues(ActiveSpace.medium)) {
                        item { ClassCard(it, Instant.now(), {}) }
                    }
                }
        }
    }
}

@ActivePreviews
@Composable
private fun RefreshingContentPreview() {
    ActiveTheme {
        Surface(Modifier.height(320.dp)) {
            RefreshableStateContent(
                ScreenState.Ready(PreviewData.fitnessClass, refreshing = true), {}) {
                    LazyColumn(contentPadding = PaddingValues(ActiveSpace.medium)) {
                        item { ClassCard(it, Instant.now(), {}) }
                    }
                }
        }
    }
}
