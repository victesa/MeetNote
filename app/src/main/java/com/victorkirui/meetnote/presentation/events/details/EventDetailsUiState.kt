package com.victorkirui.meetnote.presentation.events.details

import com.victorkirui.meetnote.domain.model.EventDetailsModel

sealed interface EventDetailsUiState {
    data object Loading : EventDetailsUiState
    data class Success(val event: EventDetailsModel) : EventDetailsUiState
    data class Error(val message: String) : EventDetailsUiState
}
