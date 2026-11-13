package com.virginactive.features.timetable

import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.virginactive.core.model.*

@Composable
fun TimetableRoute(viewModel: TimetableViewModel, openClass: (ClassRef) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.onAction(TimetableAction.Refresh) }
    TimetableScreen(
        state,
        { action ->
            when (action) {
                is TimetableAction.OpenClass -> openClass(action.ref)
                else -> viewModel.onAction(action)
            }
        })
}
