package com.virginactive.core.ui

import androidx.compose.ui.graphics.vector.ImageVector
import com.virginactive.core.designsystem.ActiveIcons
import com.virginactive.core.designsystem.ActiveStatusTone
import com.virginactive.core.model.Availability
import com.virginactive.core.model.BookingStatus
import com.virginactive.core.model.FitnessClass
import java.time.Instant

data class ClassStatusUi(val label: String, val tone: ActiveStatusTone, val icon: ImageVector?)

fun FitnessClass.status(now: Instant): ClassStatusUi =
    when {
        availability == Availability.CANCELLED ->
            ClassStatusUi("Cancelled", ActiveStatusTone.Unavailable, ActiveIcons.Info)
        hasStarted(now) ->
            ClassStatusUi(
                if (endsAt.toInstant().isAfter(now)) "In progress" else "Finished",
                ActiveStatusTone.Unavailable,
                null)
        bookingStatus == BookingStatus.BOOKED ->
            ClassStatusUi("Booked", ActiveStatusTone.Success, ActiveIcons.Success)
        bookingStatus == BookingStatus.WAITLISTED ->
            ClassStatusUi("Waitlisted", ActiveStatusTone.Waitlist, ActiveIcons.Schedule)
        availability == Availability.FULL ->
            ClassStatusUi("Full · waitlist open", ActiveStatusTone.Waitlist, null)
        availability == Availability.UNKNOWN || bookingStatus == BookingStatus.UNKNOWN ->
            ClassStatusUi("Unavailable", ActiveStatusTone.Unavailable, null)
        else ->
            ClassStatusUi(
                if (available == 1) "1 spot left" else "$available spots left",
                ActiveStatusTone.Info,
                null)
    }
