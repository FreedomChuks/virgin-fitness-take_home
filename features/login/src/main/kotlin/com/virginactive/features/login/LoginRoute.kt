package com.virginactive.features.login

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.virginactive.core.model.*

@Composable
fun LoginRoute(viewModel: LoginViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LoginScreen(state, viewModel::onAction)
}
