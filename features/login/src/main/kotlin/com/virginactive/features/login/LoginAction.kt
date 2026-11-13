package com.virginactive.features.login
sealed interface LoginAction {
    data class UsernameChanged(val value: String) : LoginAction

    data class PasswordChanged(val value: String) : LoginAction

    data object TogglePasswordVisibility : LoginAction

    data object Submit : LoginAction
}
