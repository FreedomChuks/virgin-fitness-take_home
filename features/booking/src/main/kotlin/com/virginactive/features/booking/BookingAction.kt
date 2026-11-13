package com.virginactive.features.booking
sealed interface BookingAction {
    data object Refresh : BookingAction

    data object RequestBooking : BookingAction

    data object RequestCancellation : BookingAction

    data object DismissConfirmation : BookingAction

    data object ConfirmBooking : BookingAction

    data object ConfirmCancellation : BookingAction

    data object AddToCalendar : BookingAction
}

enum class BookingConfirmation {
    BOOK,
    CANCEL
}
