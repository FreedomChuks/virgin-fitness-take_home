package com.virginactive.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virginactive.core.domain.Result
import com.virginactive.core.domain.repositories.AuthRepository
import com.virginactive.core.ui.userMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LoginState(
    val username: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val busy: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class LoginViewModel @Inject constructor(private val auth: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onAction(action: LoginAction) {
        if (_state.value.busy) return
        when (action) {
            is LoginAction.UsernameChanged ->
                _state.update { it.copy(username = action.value, error = null) }
            is LoginAction.PasswordChanged ->
                _state.update { it.copy(password = action.value, error = null) }
            LoginAction.TogglePasswordVisibility ->
                _state.update { it.copy(passwordVisible = !it.passwordVisible) }
            LoginAction.Submit -> login()
        }
    }

    private fun login() {
        val username = _state.value.username
        val password = _state.value.password
        if (username.isBlank() || password.isBlank()) {
            _state.update { it.copy(error = "Enter your email and password.") }
            return
        }
        _state.update { it.copy(busy = true, error = null) }
        viewModelScope.launch {
            when (val result = auth.login(username, password)) {
                is Result.Success -> _state.value = LoginState()
                is Result.Failure ->
                    _state.update { it.copy(busy = false, error = result.error.userMessage()) }
            }
        }
    }
}
