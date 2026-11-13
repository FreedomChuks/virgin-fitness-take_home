package com.virginactive.features.home

import com.virginactive.core.model.HomeAction

sealed interface HomeUiAction {
    data object Refresh : HomeUiAction

    data class Navigate(val destination: HomeAction) : HomeUiAction
}
