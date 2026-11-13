package com.virginactive.core.designsystem

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun ActiveInfoRow(
    icon: ImageVector,
    value: String,
    modifier: Modifier = Modifier,
    label: String? = null,
) {
    Row(
        modifier.semantics(mergeDescendants = true) {},
        verticalAlignment = if (label == null) Alignment.CenterVertically else Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(ActiveSpace.smallMedium)) {
            Icon(
                icon,
                contentDescription = null,
                modifier =
                    Modifier.padding(top = if (label == null) 0.dp else ActiveSpace.xxs)
                        .size(ActiveSize.icon),
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Column(verticalArrangement = Arrangement.spacedBy(ActiveSpace.xxs)) {
                if (label != null) {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(value, style = MaterialTheme.typography.bodyLarge)
            }
        }
}

@ActivePreviews
@Composable
private fun ActiveInfoRowPreview() {
    ActiveTheme {
        Surface {
            Column(
                Modifier.padding(ActiveSpace.medium),
                verticalArrangement = Arrangement.spacedBy(ActiveSpace.medium)) {
                    ActiveInfoRow(ActiveIcons.Event, "Friday, 13 November", label = "Date")
                    ActiveInfoRow(ActiveIcons.Location, "96 Beach Rd, Sea Point, Cape Town, 8005")
                    ActiveInfoRow(ActiveIcons.Schedule, "Open today · 05:00 – 22:00")
                }
        }
    }
}
