package com.virginactive.features.home

import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.virginactive.core.model.*

@Composable
fun HomeRoute(viewModel: HomeViewModel, onNavigate: (HomeAction) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.onAction(HomeUiAction.Refresh) }
    HomeScreen(state) { action ->
        when (action) {
            is HomeUiAction.Navigate -> onNavigate(action.destination)
            HomeUiAction.Refresh -> viewModel.onAction(action)
        }
    }
}
