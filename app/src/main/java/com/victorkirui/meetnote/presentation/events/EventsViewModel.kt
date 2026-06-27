package com.victorkirui.meetnote.presentation.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victorkirui.meetnote.domain.model.EventsSummary
import com.victorkirui.meetnote.domain.usecase.event.GetAllEventsUseCase
import com.victorkirui.meetnote.presentation.state.ProfileType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class EventsViewModel(
    private val getAllEventsUseCase: GetAllEventsUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedTab = MutableStateFlow<ProfileType?>(null)

    val uiState: StateFlow<EventsUiState> = combine(
        _searchQuery,
        _selectedTab,
        getAllEventsUseCase()
    ) { query, tab, result ->
        val state = result.fold(
            onSuccess = { events ->
                val filtered = events.filter { event ->
                    (query.isEmpty() || event.eventName.contains(query, ignoreCase = true)) &&
                    (tab == null || event.eventType == tab.name)
                }
                EventsListState.Success(groupEvents(filtered))
            },
            onFailure = {
                EventsListState.Error(it.message ?: "Unknown error")
            }
        )
        EventsUiState(
            eventsListState = state,
            searchQuery = query,
            selectedTab = tab
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EventsUiState()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onTabSelected(tab: ProfileType?) {
        _selectedTab.value = tab
    }

    private fun groupEvents(events: List<EventsSummary>): Map<String, List<EventsSummary>> {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // Adjust based on your DB format
        val now = LocalDate.now()
        
        return events.sortedByDescending { it.eventDate }.groupBy { event ->
            try {
                val date = LocalDate.parse(event.eventDate, dateFormatter)
                when {
                    date.year == now.year && date.month == now.month -> "This Month"
                    date.year == now.year -> date.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                    else -> "${date.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${date.year}"
                }
            } catch (e: Exception) {
                "Other"
            }
        }
    }
}
