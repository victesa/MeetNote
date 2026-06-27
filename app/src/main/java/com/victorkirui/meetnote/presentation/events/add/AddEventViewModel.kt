package com.victorkirui.meetnote.presentation.events.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victorkirui.meetnote.domain.model.AddEventModel
import com.victorkirui.meetnote.domain.repository.EventsRepository
import com.victorkirui.meetnote.domain.usecase.event.SaveEventUseCase
import com.victorkirui.meetnote.presentation.state.ProfileType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddEventViewModel(
    savedStateHandle: SavedStateHandle,
    private val saveEventUseCase: SaveEventUseCase,
    private val eventsRepository: EventsRepository
) : ViewModel() {

    private val eventId: Long = savedStateHandle.get<Long>("event_id") ?: -1L

    private val _uiState = MutableStateFlow(AddEventUiState())
    val uiState = _uiState.asStateFlow()

    private val _submissionState = MutableStateFlow<AddEventFormSubmissionState>(AddEventFormSubmissionState.Idle)
    val submissionState = _submissionState.asStateFlow()

    private val _uiEvents = Channel<AddEventUiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    init {
        if (eventId != -1L) {
            fetchEventDetails()
        }
    }

    private fun fetchEventDetails() {
        viewModelScope.launch {
            eventsRepository.getEventDetails(eventId).collect { result ->
                result.onSuccess { details ->
                    _uiState.update { it.copy(
                        eventName = details.name,
                        eventType = if (details.eventType == ProfileType.WORK.name) ProfileType.WORK else ProfileType.SOCIAL,
                        eventDate = details.date,
                        location = details.location ?: "",
                        notes = details.notes ?: ""
                    ) }
                }
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(eventName = name) }
    }

    fun onTypeChange(type: ProfileType) {
        _uiState.update { it.copy(eventType = type) }
    }

    fun onDateChange(date: String) {
        _uiState.update { it.copy(eventDate = date) }
    }

    fun onLocationChange(location: String) {
        _uiState.update { it.copy(location = location) }
    }

    fun onNotesChange(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun saveEvent() {
        val state = _uiState.value
        if (!state.canSave) return

        _submissionState.value = AddEventFormSubmissionState.Saving
        viewModelScope.launch {
            val result = saveEventUseCase(
                AddEventModel(
                    id = if (eventId == -1L) 0L else eventId,
                    name = state.eventName,
                    date = state.eventDate,
                    location = state.location.ifBlank { null },
                    eventType = state.eventType.name,
                    notes = state.notes.ifBlank { null }
                )
            )

            result.onSuccess {
                _submissionState.value = AddEventFormSubmissionState.Success
                _uiEvents.send(AddEventUiEvent.NavigateBack)
            }
            result.onFailure { error ->
                _submissionState.value = AddEventFormSubmissionState.Error(error.message ?: "Failed to save event")
                _uiEvents.send(AddEventUiEvent.ShowToast(error.message ?: "Failed to save event"))
            }
        }
    }
}
