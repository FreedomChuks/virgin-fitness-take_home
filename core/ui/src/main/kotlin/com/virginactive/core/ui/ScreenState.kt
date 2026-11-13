package com.virginactive.core.ui

import com.virginactive.core.domain.AppFailure

sealed interface ScreenState<out T> {
    data object Loading : ScreenState<Nothing>

    data class Ready<T>(
        val value: T,
        val refreshing: Boolean = false,
        val refreshError: String? = null
    ) : ScreenState<T>

    data class Failed(val message: String) : ScreenState<Nothing>
}

fun AppFailure.userMessage(): String = message + (requestId?.let { "\nReference: $it" } ?: "")

fun <T> ScreenState<T>.refreshing(): ScreenState<T> =
    when (this) {
        is ScreenState.Ready -> copy(refreshing = true, refreshError = null)
        else -> ScreenState.Loading
    }

fun <T> ScreenState<T>.failed(error: AppFailure): ScreenState<T> =
    when (this) {
        is ScreenState.Ready -> copy(refreshing = false, refreshError = error.userMessage())
        else -> ScreenState.Failed(error.userMessage())
    }
