package com.victorkirui.meetnote.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victorkirui.meetnote.domain.repository.ContactsRepository
import com.victorkirui.meetnote.domain.repository.EventsRepository
import com.victorkirui.meetnote.domain.repository.MyProfileRepository
import com.victorkirui.meetnote.presentation.home.state.*
import com.victorkirui.meetnote.presentation.state.ProfileType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenViewModel(
    private val myProfileRepository: MyProfileRepository,
    private val eventsRepository: EventsRepository,
    private val contactsRepository: ContactsRepository
) : ViewModel() {

    private val _activeProfileTab = MutableStateFlow(ProfileType.WORK)

    private val socialProfileFlow = myProfileRepository.getSocialProfile().map { result ->
        result.fold(
            onSuccess = { ProfileState.Success(it.toHomeProfileDisplayUiModel()) },
            onFailure = { ProfileState.Error(it.message ?: "An unknown error occurred") }
        )
    }

    private val workProfileFlow = myProfileRepository.getWorkProfile().map { result ->
        result.fold(
            onSuccess = { ProfileState.Success(it.toHomeProfileDisplayUiModel()) },
            onFailure = { ProfileState.Error(it.message ?: "An unknown error occurred") }
        )
    }

    private val profileUiFlow: Flow<ProfileState> = _activeProfileTab
        .flatMapLatest { tab ->
            if (tab == ProfileType.WORK) workProfileFlow else socialProfileFlow
        }

    private val eventsUiFlow: Flow<EventsState> = eventsRepository.getAllEventsWithSessions().map { result ->
        result.fold(
            onSuccess = { EventsState.Success(it.map { event -> event.toUi() }) },
            onFailure = { EventsState.Error(it.message ?: "An unexpected error occurred") }
        )
    }

    private val contactsUiFlow: Flow<ContactsState> = contactsRepository.getContactSessionSummaryList().map { result ->
        result.fold(
            onSuccess = { ContactsState.Success(it.map { contact -> contact.toUiState() }) },
            onFailure = { ContactsState.Error(it.message ?: "An unexpected error occurred") }
        )
    }

    // Unified flow that combines everything into one single UI state for the screen
    val uiState: StateFlow<HomeScreenUiState> = combine(
        _activeProfileTab,
        profileUiFlow,
        eventsUiFlow,
        contactsUiFlow
    ) { activeTab, profile, events, contacts ->
        HomeScreenUiState(
            profileState = profile,
            eventsState = events,
            contactsState = contacts,
            activeProfileTab = activeTab
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeScreenUiState()
    )

    fun onTabSwitched(newTab: ProfileType) {
        _activeProfileTab.value = newTab
    }
}