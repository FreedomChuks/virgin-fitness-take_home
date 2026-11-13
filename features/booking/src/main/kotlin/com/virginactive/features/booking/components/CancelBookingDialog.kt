package com.virginactive.features.booking.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.virginactive.core.designsystem.*

@Composable
internal fun CancelBookingDialog(
    waitlisted: Boolean,
    lateCancellation: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon =
            if (lateCancellation) {
                { Icon(ActiveIcons.Warning, contentDescription = null) }
            } else null,
        iconContentColor = MaterialTheme.colorScheme.error,
        title = { Text(if (waitlisted) "Leave the waitlist?" else "Cancel booking?") },
        text = {
            Text(
                when {
                    lateCancellation ->
                        "This class starts within 12 hours. Cancelling may forfeit any " +
                            "associated cost."
                    waitlisted -> "You'll lose your place in the queue."
                    else -> "Your place will be released. You can book again if one is free."
                })
        },
        confirmButton = {
            ActiveTextButton(
                if (waitlisted) "Leave waitlist" else "Cancel booking",
                onConfirm,
                destructive = true)
        },
        dismissButton = { ActiveTextButton("Keep my place", onDismiss) },
    )
}
