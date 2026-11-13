package com.virginactive.features.booking

import com.virginactive.core.model.*
import java.time.Instant

internal data class BookingActions(
    val request: RequestAction? = null,
    val addToCalendar: Boolean = false,
    val release: ReleaseAction? = null,
    val mutating: Boolean = false,
    val blockedByRefresh: Boolean = false,
    val enabled: Boolean = true,
) {
    val isEmpty: Boolean
        get() = request == null && !addToCalendar && release == null
}

internal enum class RequestAction {
    Book,
    JoinWaitlist,
}

internal enum class ReleaseAction {
    CancelBooking,
    LeaveWaitlist,
}

internal fun bookingActions(
    fitnessClass: FitnessClass,
    state: BookingState,
    now: Instant,
    mutating: Boolean,
): BookingActions {
    val upcoming = !fitnessClass.hasStarted(now)
    val status = fitnessClass.bookingStatus
    val request =
        when {
            !upcoming || status != BookingStatus.NONE -> null
            fitnessClass.availability == Availability.AVAILABLE -> RequestAction.Book
            fitnessClass.availability == Availability.FULL -> RequestAction.JoinWaitlist
            else -> null
        }
    val release =
        when {
            !upcoming || state.needsRefresh -> null
            status == BookingStatus.BOOKED -> ReleaseAction.CancelBooking
            status == BookingStatus.WAITLISTED -> ReleaseAction.LeaveWaitlist
            else -> null
        }
    return BookingActions(
        request = request,
        addToCalendar = upcoming && status == BookingStatus.BOOKED,
        release = release,
        mutating = mutating,
        blockedByRefresh = state.needsRefresh,
        enabled = !state.busy && !state.needsRefresh,
    )
}
