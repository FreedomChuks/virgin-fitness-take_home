package com.virginactive.features.booking

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.virginactive.core.designsystem.ActiveScreenPreviews
import com.virginactive.core.designsystem.ActiveTheme
import com.virginactive.core.model.Booking
import com.virginactive.core.model.BookingStatus
import com.virginactive.core.model.FitnessClass
import com.virginactive.core.ui.ScreenState
import com.virginactive.core.ui.preview.PreviewData

@Composable
private fun Preview(state: BookingState) {
    ActiveTheme { Surface { BookingScreen(state, {}) } }
}

private fun ready(fitnessClass: FitnessClass) = ScreenState.Ready(fitnessClass)

@ActiveScreenPreviews
@Composable
private fun AvailableClassPreview() =
    Preview(BookingState(content = ready(PreviewData.fitnessClass)))

@ActiveScreenPreviews
@Composable
private fun FullClassPreview() = Preview(BookingState(content = ready(PreviewData.fullClass)))

@ActiveScreenPreviews
@Composable
private fun BookingConfirmedPreview() =
    Preview(
        BookingState(
            content = ready(PreviewData.bookedClass),
            receipt =
                Booking("90ba9435-5093-4884", BookingStatus.BOOKED, null, PreviewData.bookedClass)))

@ActiveScreenPreviews
@Composable
private fun WaitlistedPreview() =
    Preview(
        BookingState(
            content = ready(PreviewData.waitlistedClass),
            receipt =
                Booking(
                    "preview-waitlist", BookingStatus.WAITLISTED, 3, PreviewData.waitlistedClass)))

@ActiveScreenPreviews
@Composable
private fun LateCancellationPreview() =
    Preview(BookingState(content = ready(PreviewData.bookedSoonClass)))

@ActiveScreenPreviews
@Composable
private fun BookingBusyPreview() =
    Preview(BookingState(content = ready(PreviewData.fitnessClass), busy = true))

@ActiveScreenPreviews
@Composable
private fun BookingFailurePreview() =
    Preview(
        BookingState(
            content = ready(PreviewData.fitnessClass),
            message = "Connection lost.\nRefresh class status before trying another action.",
            needsRefresh = true))

@ActiveScreenPreviews
@Composable
private fun StartedClassPreview() =
    Preview(BookingState(content = ready(PreviewData.finishedClass)))

@ActiveScreenPreviews
@Composable
private fun CancelledClassPreview() =
    Preview(BookingState(content = ready(PreviewData.cancelledClass)))

@ActiveScreenPreviews
@Composable
private fun LongTitlePreview() = Preview(BookingState(content = ready(PreviewData.longTitleClass)))

@ActiveScreenPreviews
@Composable
private fun ConfirmBookingDialogPreview() =
    Preview(
        BookingState(
            content = ready(PreviewData.fitnessClass), confirmation = BookingConfirmation.BOOK))

@ActiveScreenPreviews
@Composable
private fun ConfirmCancelDialogPreview() =
    Preview(
        BookingState(
            content = ready(PreviewData.bookedSoonClass),
            confirmation = BookingConfirmation.CANCEL))
