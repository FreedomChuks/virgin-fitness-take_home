package com.virginactive.features.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.virginactive.core.designsystem.*
import com.virginactive.core.model.HomeTile
import java.time.format.DateTimeFormatter

private val dayFormat = DateTimeFormatter.ofPattern("EEE d MMM")
private val timeFormat = DateTimeFormatter.ofPattern("HH:mm")

@Composable
internal fun HomeTileCard(tile: HomeTile, onOpen: (() -> Unit)?, modifier: Modifier = Modifier) {
    ActiveCard(modifier.width(232.dp).fillMaxHeight(), onClick = onOpen) {
        Column(
            Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(ActiveSpace.small)) {
                tile.startsAt?.let {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            it.format(timeFormat),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary)
                        Text(
                            "  ${it.format(dayFormat)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = ActiveSpace.xxs))
                    }
                }
                Text(tile.title, style = MaterialTheme.typography.titleMedium)
                tile.subtitle?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.weight(1f))
                tile.badge?.let { badge -> TileBadge(badge) }
                if (onOpen != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            tile.actionLabel ?: "View class",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary)
                        Icon(
                            ActiveIcons.Forward,
                            contentDescription = null,
                            modifier = Modifier.size(ActiveSize.icon),
                            tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
    }
}

@Composable
private fun TileBadge(badge: String) {
    val tone = badgeTone(badge)
    ActiveBadge(
        badge,
        tone = tone,
        icon =
            when (tone) {
                ActiveStatusTone.Success -> ActiveIcons.Success
                ActiveStatusTone.Waitlist -> ActiveIcons.Schedule
                else -> null
            })
}

private fun badgeTone(badge: String): ActiveStatusTone =
    when (badge.trim().lowercase()) {
        "booked" -> ActiveStatusTone.Success
        "waitlisted",
        "waitlist" -> ActiveStatusTone.Waitlist
        else -> ActiveStatusTone.Info
    }
