package com.virginactive.features.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virginactive.core.domain.*
import com.virginactive.core.domain.Result
import com.virginactive.core.domain.repositories.FitnessRepository
import com.virginactive.core.model.*
import com.virginactive.core.ui.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class BookingState(
    val content: ScreenState<FitnessClass> = ScreenState.Loading,
    val busy: Boolean = false,
    val receipt: Booking? = null,
    val message: String? = null,
    val needsRefresh: Boolean = false,
    val confirmation: BookingConfirmation? = null,
)

@HiltViewModel(assistedFactory = BookingViewModel.Factory::class)
class BookingViewModel
@AssistedInject
constructor(
    private val repository: FitnessRepository,
    private val getClassUseCase: GetClassUseCase,
    private val bookClassUseCase: BookClassUseCase,
    @Assisted private val ref: ClassRef
) : ViewModel() {
    private val _state = MutableStateFlow(BookingState())
    val state = _state.asStateFlow()

    init {
        refresh()
    }

    fun onAction(action: BookingAction) {
        when (action) {
            BookingAction.Refresh -> refresh()
            BookingAction.RequestBooking -> requestConfirmation(BookingConfirmation.BOOK)
            BookingAction.RequestCancellation -> requestConfirmation(BookingConfirmation.CANCEL)
            BookingAction.DismissConfirmation -> _state.update { it.copy(confirmation = null) }
            BookingAction.ConfirmBooking ->
                if (_state.value.confirmation == BookingConfirmation.BOOK) {
                    _state.update { it.copy(confirmation = null) }
                    book()
                }
            BookingAction.ConfirmCancellation ->
                if (_state.value.confirmation == BookingConfirmation.CANCEL) {
                    _state.update { it.copy(confirmation = null) }
                    cancel()
                }
            BookingAction.AddToCalendar -> Unit
        }
    }

    private fun requestConfirmation(confirmation: BookingConfirmation) {
        val current = _state.value
        if (current.busy || current.needsRefresh || current.content !is ScreenState.Ready) return
        val status = current.content.value.bookingStatus
        if (confirmation == BookingConfirmation.BOOK && status != BookingStatus.NONE) return
        if (confirmation == BookingConfirmation.CANCEL &&
            status !in setOf(BookingStatus.BOOKED, BookingStatus.WAITLISTED))
            return
        _state.update { it.copy(confirmation = confirmation) }
    }

    private fun refresh() {
        if (_state.value.busy) return
        _state.value =
            _state.value.copy(
                busy = true,
                message = null,
                confirmation = null,
                content = _state.value.content.refreshing())
        viewModelScope.launch {
            when (val result = getClassUseCase(ref)) {
                is Result.Success ->
                    _state.value =
                        _state.value.copy(
                            content = ScreenState.Ready(result.data),
                            busy = false,
                            needsRefresh = false)
                is Result.Failure ->
                    _state.value =
                        _state.value.copy(
                            content = _state.value.content.failed(result.error), busy = false)
            }
        }
    }

    private fun book() = mutate { fitnessClass ->
        bookClassUseCase(fitnessClass).flatMap { receipt ->
            _state.value =
                BookingState(content = ScreenState.Ready(receipt.fitnessClass), receipt = receipt)
            Result.Success(Unit)
        }
    }

    private fun cancel() = mutate { fitnessClass ->
        repository.cancel(ref).flatMap {
            _state.value =
                BookingState(
                    content =
                        ScreenState.Ready(fitnessClass.copy(bookingStatus = BookingStatus.NONE)),
                    busy = true,
                    message = "Booking cancelled.")
            getClassUseCase(ref).flatMap { updated ->
                _state.value =
                    BookingState(
                        content = ScreenState.Ready(updated), message = "Booking cancelled.")
                Result.Success(Unit)
            }
        }
    }

    private fun mutate(action: suspend (FitnessClass) -> Result<Unit>) {
        val current = _state.value
        val fitnessClass = (current.content as? ScreenState.Ready)?.value ?: return
        if (current.busy || current.needsRefresh) return
        _state.value = current.copy(busy = true, message = null, confirmation = null)
        viewModelScope.launch {
            when (val result = action(fitnessClass)) {
                is Result.Success -> Unit
                is Result.Failure ->
                    _state.value =
                        _state.value.copy(
                            busy = false,
                            needsRefresh = true,
                            message =
                                result.error.userMessage() +
                                    "\nRefresh class status before trying another action.")
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(ref: ClassRef): BookingViewModel
    }
}
