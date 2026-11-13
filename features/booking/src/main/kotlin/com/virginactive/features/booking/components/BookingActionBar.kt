package com.virginactive.features.booking.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.virginactive.core.designsystem.*
import com.virginactive.features.booking.*

@Composable
internal fun BookingActionBar(
    actions: BookingActions,
    onAction: (BookingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (actions.isEmpty) return
    Surface(modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface) {
        Column {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Column(
                Modifier.fillMaxWidth()
                    .padding(horizontal = ActiveSpace.screen, vertical = ActiveSpace.smallMedium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(ActiveSpace.small)) {
                    actions.request?.let { request ->
                        val waitlist = request == RequestAction.JoinWaitlist
                        ActiveButton(
                            label =
                                when {
                                    actions.mutating && waitlist -> "Joining waitlist…"
                                    actions.mutating -> "Booking…"
                                    waitlist -> "Join waitlist"
                                    else -> "Book class"
                                },
                            onClick = { onAction(BookingAction.RequestBooking) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = actions.enabled,
                            busy = actions.mutating)
                    }
                    if (actions.addToCalendar) {
                        ActiveButton(
                            "Add to calendar",
                            { onAction(BookingAction.AddToCalendar) },
                            Modifier.fillMaxWidth(),
                            leadingIcon = ActiveIcons.Event)
                        Text(
                            "Opens your calendar app, where you can save the event and set a " +
                                "reminder.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center)
                    }
                    when (actions.release) {
                        ReleaseAction.CancelBooking ->
                            ActiveTextButton(
                                if (actions.mutating) "Cancelling…" else "Cancel booking",
                                { onAction(BookingAction.RequestCancellation) },
                                enabled = actions.enabled,
                                destructive = true)
                        ReleaseAction.LeaveWaitlist ->
                            ActiveSecondaryButton(
                                if (actions.mutating) "Leaving waitlist…" else "Leave waitlist",
                                { onAction(BookingAction.RequestCancellation) },
                                Modifier.fillMaxWidth(),
                                enabled = actions.enabled)
                        null -> Unit
                    }
                    if (actions.blockedByRefresh) {
                        Text(
                            "Pull down to refresh the class status before trying again.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center)
                    }
                }
        }
    }
}
