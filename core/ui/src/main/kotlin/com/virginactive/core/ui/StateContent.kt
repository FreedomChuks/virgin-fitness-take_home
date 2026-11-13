package com.virginactive.core.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import com.virginactive.core.designsystem.*

@Composable
fun <T> StateContent(state: ScreenState<T>, retry: () -> Unit, content: @Composable (T) -> Unit) {
    when (state) {
        ScreenState.Loading ->
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).semantics(
                    mergeDescendants = true) {
                        liveRegion = LiveRegionMode.Polite
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                    CircularProgressIndicator(strokeWidth = ActiveSpace.xs)
                    Text(
                        "Loading…",
                        Modifier.padding(top = ActiveSpace.medium),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
        is ScreenState.Failed ->
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center) {
                    ActiveEmptyState(
                        title = "We couldn't load this",
                        body = state.message,
                        modifier = Modifier.fillMaxWidth(),
                        icon = ActiveIcons.CloudOff,
                        tone = ActiveStatusTone.Error,
                        actionLabel = "Try again",
                        onAction = retry)
                }
        is ScreenState.Ready -> content(state.value)
    }
}
