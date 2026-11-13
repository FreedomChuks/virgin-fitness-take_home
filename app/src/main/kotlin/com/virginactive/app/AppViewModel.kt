package com.virginactive.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virginactive.core.domain.Result
import com.virginactive.core.domain.repositories.AuthRepository
import com.virginactive.core.ui.userMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AppAction {
    data object SignOut : AppAction
}

@HiltViewModel
class AppViewModel @Inject constructor(private val auth: AuthRepository) : ViewModel() {
    val signedIn = auth.signedIn
    private val _signOutError = MutableStateFlow<String?>(null)
    val signOutError = _signOutError.asStateFlow()

    fun onAction(action: AppAction) {
        when (action) {
            AppAction.SignOut -> signOut()
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            _signOutError.value = null
            when (val result = auth.logout()) {
                is Result.Success -> Unit
                is Result.Failure -> _signOutError.value = result.error.userMessage()
            }
        }
    }
}
