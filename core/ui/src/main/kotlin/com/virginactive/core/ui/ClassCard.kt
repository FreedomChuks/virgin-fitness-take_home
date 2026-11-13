package com.virginactive.core.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.virginactive.core.designsystem.*
import com.virginactive.core.model.*
import java.time.Instant

@Composable
fun ClassCard(
    fitnessClass: FitnessClass,
    now: Instant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val status = fitnessClass.status(now)
    val muted = status.tone == ActiveStatusTone.Unavailable
    ActiveCard(
        modifier.fillMaxWidth().semantics {
            contentDescription =
                "${fitnessClass.title}, ${fitnessClass.startLabel} to ${fitnessClass.endLabel}, " +
                    "with ${fitnessClass.trainer}, ${fitnessClass.categoryLabel}. ${status.label}"
        },
        onClick = onClick,
        contentPadding =
            PaddingValues(
                start = ActiveSpace.medium,
                top = ActiveSpace.medium,
                bottom = ActiveSpace.medium,
                end = ActiveSpace.small)) {
            Row(
                Modifier.height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(ActiveSpace.smallMedium),
                verticalAlignment = Alignment.Top) {
                    Column(Modifier.widthIn(min = 52.dp)) {
                        Text(
                            fitnessClass.startLabel,
                            style = MaterialTheme.typography.titleMedium,
                            color =
                                if (muted) MaterialTheme.colorScheme.onSurfaceVariant
                                else MaterialTheme.colorScheme.primary)
                        Text(
                            fitnessClass.durationLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    VerticalDivider(
                        Modifier.fillMaxHeight(), color = MaterialTheme.colorScheme.outlineVariant)
                    Column(
                        Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(ActiveSpace.xs)) {
                            Text(
                                fitnessClass.title,
                                style = MaterialTheme.typography.titleMedium,
                                color =
                                    if (muted) MaterialTheme.colorScheme.onSurfaceVariant
                                    else MaterialTheme.colorScheme.onSurface)
                            Text(
                                "${fitnessClass.trainer} · ${fitnessClass.categoryLabel}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            ActiveBadge(
                                status.label,
                                Modifier.padding(top = ActiveSpace.xs),
                                tone = status.tone,
                                icon = status.icon)
                        }
                    Icon(
                        ActiveIcons.Forward,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
        }
}
