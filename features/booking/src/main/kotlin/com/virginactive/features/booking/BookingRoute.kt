package com.virginactive.features.booking

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.virginactive.core.model.*
import com.virginactive.core.ui.ScreenState

@Composable
fun BookingRoute(viewModel: BookingViewModel, addToCalendar: (FitnessClass) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    BookingScreen(
        state,
        { action ->
            when (action) {
                BookingAction.AddToCalendar ->
                    (state.content as? ScreenState.Ready)
                        ?.value
                        ?.takeIf { it.bookingStatus == BookingStatus.BOOKED }
                        ?.let(addToCalendar)
                else -> viewModel.onAction(action)
            }
        })
}
