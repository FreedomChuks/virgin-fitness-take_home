package com.virginactive.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import com.virginactive.core.designsystem.*

@Composable
fun <T> RefreshableStateContent(
    state: ScreenState<T>,
    onRefresh: () -> Unit,
    content: @Composable (T) -> Unit
) {
    val ready = state as? ScreenState.Ready
    val refreshing = ready?.refreshing == true
    val pullState = rememberPullToRefreshState()
    PullToRefreshBox(
        isRefreshing = refreshing,
        onRefresh = { if (!refreshing) onRefresh() },
        state = pullState,
        modifier =
            Modifier.fillMaxSize().semantics {
                customActions =
                    listOf(
                        CustomAccessibilityAction("Refresh") {
                            if (!refreshing) onRefresh()
                            true
                        })
            },
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = pullState,
                isRefreshing = refreshing,
                modifier = Modifier.align(Alignment.TopCenter),
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                color = MaterialTheme.colorScheme.primary)
        }) {
            Column(Modifier.fillMaxSize()) {
                AnimatedVisibility(ready?.refreshError != null) {
                    ready?.refreshError?.let {
                        ActiveNotice(
                            "$it\nPull down to try again.",
                            Modifier.fillMaxWidth()
                                .padding(
                                    horizontal = ActiveSpace.screen, vertical = ActiveSpace.small),
                            tone = ActiveStatusTone.Error,
                            title = "Couldn't refresh")
                    }
                }
                Box(Modifier.weight(1f)) { StateContent(state, onRefresh, content) }
            }
        }
}
