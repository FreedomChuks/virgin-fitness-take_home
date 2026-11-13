package com.virginactive.features.booking.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.virginactive.core.designsystem.*
import com.virginactive.core.model.Availability
import com.virginactive.core.model.FitnessClass
import com.virginactive.core.ui.dateLabel
import com.virginactive.core.ui.timeLabel

@Composable
internal fun BookingConfirmDialog(
    fitnessClass: FitnessClass,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val waitlist = fitnessClass.availability == Availability.FULL
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(ActiveIcons.Event, contentDescription = null) },
        title = { Text(if (waitlist) "Join the waitlist?" else "Book this class?") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(ActiveSpace.smallMedium)) {
                Column(verticalArrangement = Arrangement.spacedBy(ActiveSpace.xxs)) {
                    Text(fitnessClass.title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${fitnessClass.dateLabel} · ${fitnessClass.timeLabel}",
                        style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "with ${fitnessClass.trainer}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(
                    if (waitlist)
                        "You'll only get a place if one becomes free. Your waitlist position " +
                            "is confirmed once you join."
                    else
                        "If the last place is taken before your request arrives, you'll be " +
                            "added to the waitlist instead.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        confirmButton = {
            ActiveTextButton(if (waitlist) "Join waitlist" else "Book class", onConfirm)
        },
        dismissButton = { ActiveTextButton("Not now", onDismiss) },
    )
}
