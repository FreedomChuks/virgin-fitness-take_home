package com.virginactive.core.designsystem

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ActiveEmptyState(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = ActiveIcons.Fitness,
    tone: ActiveStatusTone = ActiveStatusTone.Unavailable,
    actionLabel: String? = null,
    onAction: () -> Unit = {},
) {
    val colors = ActiveTheme.status.of(tone)
    Column(
        modifier.padding(horizontal = ActiveSpace.large, vertical = ActiveSpace.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ActiveSpace.smallMedium)) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = colors.container,
                contentColor = colors.content) {
                    Icon(
                        icon,
                        contentDescription = null,
                        modifier = Modifier.padding(ActiveSpace.medium).size(ActiveSize.iconLarge))
                }
            Column(
                Modifier.semantics(mergeDescendants = true) {
                    if (tone == ActiveStatusTone.Error) liveRegion = LiveRegionMode.Polite
                },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(ActiveSpace.xs)) {
                    Text(
                        title,
                        Modifier.semantics { heading() },
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center)
                    Text(
                        body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center)
                }
            if (actionLabel != null) {
                ActiveSecondaryButton(
                    actionLabel, onAction, Modifier.padding(top = ActiveSpace.small))
            }
        }
}

@ActivePreviews
@Composable
private fun ActiveEmptyStatePreview() {
    ActiveTheme {
        Surface {
            Column {
                ActiveEmptyState(
                    "No classes on Saturday",
                    "Try another day, or browse the whole week.",
                    Modifier.fillMaxWidth(),
                    actionLabel = "Show all days")
                ActiveEmptyState(
                    "We couldn't load this",
                    "Check your connection and try again.",
                    Modifier.fillMaxWidth(),
                    icon = ActiveIcons.CloudOff,
                    tone = ActiveStatusTone.Error,
                    actionLabel = "Try again")
            }
        }
    }
}
