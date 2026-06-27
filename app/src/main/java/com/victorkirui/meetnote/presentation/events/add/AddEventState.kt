package com.victorkirui.meetnote.presentation.events.add

import com.victorkirui.meetnote.presentation.state.ProfileType

data class AddEventUiState(
    val eventName: String = "",
    val eventType: ProfileType = ProfileType.WORK,
    val eventDate: String = "",
    val location: String = "",
    val notes: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val canSave: Boolean
        get() = eventName.isNotBlank() && eventDate.isNotBlank()
}

sealed interface AddEventFormSubmissionState {
    data object Idle : AddEventFormSubmissionState
    data object Saving : AddEventFormSubmissionState
    data object Success : AddEventFormSubmissionState
    data class Error(val message: String) : AddEventFormSubmissionState
}

sealed interface AddEventUiEvent {
    data object NavigateBack : AddEventUiEvent
    data class ShowToast(val message: String) : AddEventUiEvent
}
