package com.virginactive.features.login

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.virginactive.core.designsystem.ActiveScreenPreviews
import com.virginactive.core.designsystem.ActiveTheme

@ActiveScreenPreviews
@Composable
private fun LoginPreview() {
    ActiveTheme { Surface { LoginScreen(LoginState(username = "member@example.com"), {}) } }
}

@ActiveScreenPreviews
@Composable
private fun LoginErrorPreview() {
    ActiveTheme {
        Surface {
            LoginScreen(
                LoginState(
                    username = "member@example.com",
                    error = "Your email or password is incorrect."),
                {})
        }
    }
}

@ActiveScreenPreviews
@Composable
private fun LoginLoadingPreview() {
    ActiveTheme {
        Surface { LoginScreen(LoginState(username = "member@example.com", busy = true), {}) }
    }
}
