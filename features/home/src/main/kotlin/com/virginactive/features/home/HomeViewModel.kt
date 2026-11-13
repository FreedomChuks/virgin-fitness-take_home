package com.virginactive.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virginactive.core.domain.Result
import com.virginactive.core.domain.repositories.FitnessRepository
import com.virginactive.core.model.HomeBlock
import com.virginactive.core.ui.ScreenState
import com.virginactive.core.ui.failed
import com.virginactive.core.ui.refreshing
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: FitnessRepository) : ViewModel() {
    private val _state = MutableStateFlow<ScreenState<List<HomeBlock>>>(ScreenState.Loading)
    val state = _state.asStateFlow()
    private var job: Job? = null

    fun onAction(action: HomeUiAction) {
        when (action) {
            HomeUiAction.Refresh -> refresh()
            is HomeUiAction.Navigate -> Unit
        }
    }

    private fun refresh() {
        job?.cancel()
        job =
            viewModelScope.launch {
                _state.value = _state.value.refreshing()
                _state.value =
                    when (val result = repository.home()) {
                        is Result.Success -> ScreenState.Ready(result.data)
                        is Result.Failure -> _state.value.failed(result.error)
                    }
            }
    }
}
