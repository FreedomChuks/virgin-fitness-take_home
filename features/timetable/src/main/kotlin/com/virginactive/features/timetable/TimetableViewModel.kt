package com.virginactive.features.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virginactive.core.domain.Result
import com.virginactive.core.domain.flatMap
import com.virginactive.core.domain.repositories.FitnessRepository
import com.virginactive.core.model.Timetable
import com.virginactive.core.ui.ScreenState
import com.virginactive.core.ui.failed
import com.virginactive.core.ui.refreshing
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class TimetableState(
    val content: ScreenState<Timetable> = ScreenState.Loading,
    val selectedDate: LocalDate? = null
)

@HiltViewModel(assistedFactory = TimetableViewModel.Factory::class)
class TimetableViewModel
@AssistedInject
constructor(
    private val repository: FitnessRepository,
    @Assisted private val clubId: String?,
) : ViewModel() {
    private val _state = MutableStateFlow(TimetableState())
    val state = _state.asStateFlow()
    private var loadJob: Job? = null
    private var loadedWeek: LocalDate? = null

    fun onAction(action: TimetableAction) {
        when (action) {
            TimetableAction.Refresh -> refresh()
            is TimetableAction.SelectDay -> {
                val table = (_state.value.content as? ScreenState.Ready)?.value ?: return
                if (action.date == null || table.days.any { it.date == action.date }) {
                    _state.update { it.copy(selectedDate = action.date) }
                }
            }
            is TimetableAction.OpenClass -> Unit
        }
    }

    private fun refresh() {
        loadJob?.cancel()
        loadJob =
            viewModelScope.launch {
                _state.update { it.copy(content = it.content.refreshing()) }
                val result =
                    if (clubId != null) repository.timetable(clubId)
                    else repository.member().flatMap { repository.timetable(it.homeClubId) }
                when (result) {
                    is Result.Success -> {
                        val table = result.data
                        val selected = _state.value.selectedDate
                        val valid =
                            loadedWeek == table.weekStart &&
                                (selected == null || table.days.any { it.date == selected })
                        loadedWeek = table.weekStart
                        _state.value =
                            TimetableState(
                                ScreenState.Ready(table),
                                if (valid) selected else table.selectedDate)
                    }
                    is Result.Failure ->
                        _state.update { it.copy(content = it.content.failed(result.error)) }
                }
            }
    }

    @AssistedFactory
    interface Factory {
        fun create(clubId: String?): TimetableViewModel
    }
}
