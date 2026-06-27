package com.victorkirui.meetnote.presentation.events

import com.victorkirui.meetnote.domain.model.EventsSummary
import com.victorkirui.meetnote.presentation.state.ProfileType

data class EventsUiState(
    val eventsListState: EventsListState = EventsListState.Loading,
    val searchQuery: String = "",
    val selectedTab: ProfileType? = null // null means "All"
)

sealed interface EventsListState {
    data object Loading : EventsListState
    data class Success(val groupedEvents: Map<String, List<EventsSummary>>) : EventsListState
    data class Error(val message: String) : EventsListState
}
