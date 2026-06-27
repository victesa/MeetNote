package com.victorkirui.meetnote.presentation.events.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victorkirui.meetnote.domain.usecase.event.DeleteEventUseCase
import com.victorkirui.meetnote.domain.usecase.event.GetEventDetailsUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EventDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val getEventDetailsUseCase: GetEventDetailsUseCase,
    private val deleteEventUseCase: DeleteEventUseCase
) : ViewModel() {

    private val eventId: Long = checkNotNull(savedStateHandle["event_id"])

    private val _uiState = MutableStateFlow<EventDetailsUiState>(EventDetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = Channel<EventDetailsUiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    init {
        fetchEventDetails()
    }

    private fun fetchEventDetails() {
        viewModelScope.launch {
            getEventDetailsUseCase(eventId).collect { result ->
                result.onSuccess { details ->
                    _uiState.value = EventDetailsUiState.Success(details)
                }
                result.onFailure { error ->
                    _uiState.value = EventDetailsUiState.Error(error.message ?: "An error occurred")
                }
            }
        }
    }

    fun deleteEvent(deleteContacts: Boolean) {
        viewModelScope.launch {
            deleteEventUseCase(eventId, deleteContacts).onSuccess {
                _uiEvents.send(EventDetailsUiEvent.EventDeleted)
            }
        }
    }
}

sealed interface EventDetailsUiEvent {
    data object EventDeleted : EventDetailsUiEvent
}
