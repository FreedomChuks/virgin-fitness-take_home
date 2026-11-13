package com.virginactive.features.booking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.virginactive.core.designsystem.*
import com.virginactive.core.model.*
import com.virginactive.core.ui.*
import com.virginactive.features.booking.components.*

@Composable
fun BookingScreen(state: BookingState, onAction: (BookingAction) -> Unit) {
    val now = rememberCurrentInstant()
    RefreshableStateContent(state.content, { onAction(BookingAction.Refresh) }) { fitnessClass ->
        val refreshing = (state.content as? ScreenState.Ready)?.refreshing == true
        val actions = bookingActions(fitnessClass, state, now, mutating = state.busy && !refreshing)
        Column(Modifier.fillMaxSize()) {
            Column(
                Modifier.weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = ActiveSpace.screen, vertical = ActiveSpace.medium),
                verticalArrangement = Arrangement.spacedBy(ActiveSpace.medium)) {
                    ClassHeader(fitnessClass)
                    state.message?.let {
                        if (state.needsRefresh) {
                            ActiveNotice(
                                "$it\nPull down to refresh.",
                                Modifier.fillMaxWidth(),
                                tone = ActiveStatusTone.Error,
                                title = "Check the class status")
                        } else {
                            ActiveNotice(
                                it, Modifier.fillMaxWidth(), tone = ActiveStatusTone.Success)
                        }
                    }
                    val receipt = state.receipt
                    if (receipt != null) BookingReceiptCard(receipt)
                    else ClassStatusBanner(fitnessClass, now)
                    if (fitnessClass.bookingStatus == BookingStatus.BOOKED &&
                        fitnessClass.cancellationMayForfeitCost(now)) {
                        ActiveNotice(
                            "This class starts within 12 hours. Cancelling now may forfeit any " +
                                "associated cost.",
                            Modifier.fillMaxWidth(),
                            tone = ActiveStatusTone.Waitlist,
                            icon = ActiveIcons.Warning)
                    }
                    ClassDetailsCard(fitnessClass)
                    Text(
                        "Pull down to refresh availability.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            BookingActionBar(actions, onAction)
        }
        when (state.confirmation) {
            BookingConfirmation.BOOK ->
                BookingConfirmDialog(
                    fitnessClass,
                    onConfirm = { onAction(BookingAction.ConfirmBooking) },
                    onDismiss = { onAction(BookingAction.DismissConfirmation) })
            BookingConfirmation.CANCEL ->
                CancelBookingDialog(
                    waitlisted = fitnessClass.bookingStatus == BookingStatus.WAITLISTED,
                    lateCancellation = fitnessClass.cancellationMayForfeitCost(now),
                    onConfirm = { onAction(BookingAction.ConfirmCancellation) },
                    onDismiss = { onAction(BookingAction.DismissConfirmation) })
            null -> Unit
        }
    }
}
