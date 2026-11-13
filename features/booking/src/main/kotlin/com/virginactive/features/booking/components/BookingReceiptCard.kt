package com.virginactive.features.booking.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.virginactive.core.designsystem.*
import com.virginactive.core.model.Booking
import com.virginactive.core.model.BookingStatus

@Composable
internal fun BookingReceiptCard(receipt: Booking, modifier: Modifier = Modifier) {
    val booked = receipt.status == BookingStatus.BOOKED
    val details =
        listOfNotNull(
                receipt.waitlistPosition?.let { "Waitlist position: $it" },
                "Reference: ${receipt.id}")
            .joinToString("\n")
    ActiveNotice(
        if (booked) details else "Your place is not confirmed yet.\n$details",
        modifier.fillMaxWidth(),
        tone = if (booked) ActiveStatusTone.Success else ActiveStatusTone.Waitlist,
        title = if (booked) "Booking confirmed" else "Added to the waitlist")
}
