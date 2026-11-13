package com.virginactive.features.booking.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.virginactive.core.designsystem.*
import com.virginactive.core.model.*
import com.virginactive.core.ui.dateLabel
import com.virginactive.core.ui.startLabel
import java.time.Instant

@Composable
internal fun ClassStatusBanner(
    fitnessClass: FitnessClass,
    now: Instant,
    modifier: Modifier = Modifier,
) {
    val (tone, title, body) =
        when {
            fitnessClass.availability == Availability.CANCELLED ->
                Triple(
                    ActiveStatusTone.Unavailable,
                    "This class has been cancelled",
                    "It can no longer be booked.")
            fitnessClass.hasStarted(now) ->
                Triple(
                    ActiveStatusTone.Unavailable,
                    if (fitnessClass.endsAt.toInstant().isAfter(now)) "This class has started"
                    else "This class has finished",
                    "Booking and cancellation are closed.")
            fitnessClass.bookingStatus == BookingStatus.BOOKED ->
                Triple(
                    ActiveStatusTone.Success,
                    "You're booked",
                    "Your place is confirmed for ${fitnessClass.dateLabel} at " +
                        "${fitnessClass.startLabel}.")
            fitnessClass.bookingStatus == BookingStatus.WAITLISTED ->
                Triple(
                    ActiveStatusTone.Waitlist,
                    "You're on the waitlist",
                    "Your place is not confirmed. Check back here for your status.")
            fitnessClass.availability == Availability.FULL ->
                Triple(
                    ActiveStatusTone.Waitlist,
                    "This class is full",
                    "You can join the waitlist. You'll only get a place if one becomes free.")
            fitnessClass.availability == Availability.UNKNOWN ||
                fitnessClass.bookingStatus == BookingStatus.UNKNOWN ->
                Triple(
                    ActiveStatusTone.Unavailable,
                    "Status unavailable",
                    "We can't confirm this class's status. Pull down to refresh.")
            else -> return
        }
    ActiveNotice(body, modifier.fillMaxWidth(), tone = tone, title = title)
}
