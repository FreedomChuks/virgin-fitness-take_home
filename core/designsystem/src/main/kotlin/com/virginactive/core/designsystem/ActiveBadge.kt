package com.virginactive.core.designsystem

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun ActiveBadge(
    label: String,
    modifier: Modifier = Modifier,
    tone: ActiveStatusTone = ActiveStatusTone.Info,
    icon: ImageVector? = null,
) {
    val colors = ActiveTheme.status.of(tone)
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraSmall,
        color = colors.container,
        contentColor = colors.content) {
            Row(
                Modifier.padding(horizontal = ActiveSpace.small, vertical = ActiveSpace.xs),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ActiveSpace.xs)) {
                    if (icon != null) {
                        Icon(icon, contentDescription = null, Modifier.size(ActiveSize.iconSmall))
                    }
                    Text(label, style = MaterialTheme.typography.labelMedium)
                }
        }
}

@ActivePreviews
@Composable
private fun ActiveBadgePreview() {
    ActiveTheme {
        Surface {
            Column(
                Modifier.padding(ActiveSpace.medium),
                verticalArrangement = Arrangement.spacedBy(ActiveSpace.small)) {
                    ActiveBadge(
                        "Booked", tone = ActiveStatusTone.Success, icon = ActiveIcons.Success)
                    ActiveBadge(
                        "Waitlisted", tone = ActiveStatusTone.Waitlist, icon = ActiveIcons.Schedule)
                    ActiveBadge("Full · join waitlist", tone = ActiveStatusTone.Waitlist)
                    ActiveBadge("8 spots left")
                    ActiveBadge("Finished", tone = ActiveStatusTone.Unavailable)
                    ActiveBadge("Cancelled", tone = ActiveStatusTone.Error)
                }
        }
    }
}
