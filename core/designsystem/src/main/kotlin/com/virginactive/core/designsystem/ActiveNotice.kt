package com.virginactive.core.designsystem

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics

@Composable
fun ActiveNotice(
    text: String,
    modifier: Modifier = Modifier,
    tone: ActiveStatusTone = ActiveStatusTone.Info,
    title: String? = null,
    icon: ImageVector? = defaultNoticeIcon(tone),
) {
    val colors = ActiveTheme.status.of(tone)
    Surface(
        modifier.semantics(mergeDescendants = true) {
            if (tone == ActiveStatusTone.Error) liveRegion = LiveRegionMode.Polite
        },
        shape = MaterialTheme.shapes.medium,
        color = colors.container,
        contentColor = colors.content) {
            Row(
                Modifier.padding(
                    horizontal = ActiveSpace.medium, vertical = ActiveSpace.smallMedium),
                horizontalArrangement = Arrangement.spacedBy(ActiveSpace.smallMedium)) {
                    if (icon != null) {
                        Icon(icon, contentDescription = null, Modifier.size(ActiveSize.icon))
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(ActiveSpace.xxs)) {
                        if (title != null) {
                            Text(title, style = MaterialTheme.typography.titleSmall)
                        }
                        Text(text, style = MaterialTheme.typography.bodyMedium)
                    }
                }
        }
}

private fun defaultNoticeIcon(tone: ActiveStatusTone): ImageVector =
    when (tone) {
        ActiveStatusTone.Success -> ActiveIcons.Success
        ActiveStatusTone.Waitlist -> ActiveIcons.Schedule
        ActiveStatusTone.Unavailable -> ActiveIcons.Info
        ActiveStatusTone.Error -> ActiveIcons.Warning
        ActiveStatusTone.Info -> ActiveIcons.InfoFilled
    }

@ActivePreviews
@Composable
private fun ActiveNoticePreview() {
    ActiveTheme {
        Surface {
            Column(
                Modifier.padding(ActiveSpace.medium),
                verticalArrangement = Arrangement.spacedBy(ActiveSpace.smallMedium)) {
                    ActiveNotice(
                        "See you on Friday at 07:00.",
                        Modifier.fillMaxWidth(),
                        tone = ActiveStatusTone.Success,
                        title = "You're booked")
                    ActiveNotice(
                        "A place is not confirmed until someone cancels.",
                        Modifier.fillMaxWidth(),
                        tone = ActiveStatusTone.Waitlist,
                        title = "You're on the waitlist")
                    ActiveNotice(
                        "This class has already started.",
                        Modifier.fillMaxWidth(),
                        tone = ActiveStatusTone.Unavailable)
                    ActiveNotice(
                        "We couldn't connect. Check your connection and try again.",
                        Modifier.fillMaxWidth(),
                        tone = ActiveStatusTone.Error)
                    ActiveNotice("Times are shown in club time.", Modifier.fillMaxWidth())
                }
        }
    }
}
