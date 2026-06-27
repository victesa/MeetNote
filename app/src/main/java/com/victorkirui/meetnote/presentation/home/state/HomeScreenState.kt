package com.victorkirui.meetnote.presentation.home.state

import androidx.compose.runtime.Immutable
import com.victorkirui.meetnote.domain.model.ContactSummary
import com.victorkirui.meetnote.domain.model.EventsSummary
import com.victorkirui.meetnote.domain.model.ProfileDataModel
import com.victorkirui.meetnote.presentation.state.ProfileType

// Combined Screen State
@Immutable
data class HomeScreenUiState(
    val profileState: ProfileState = ProfileState.Loading,
    val eventsState: EventsState = EventsState.Loading,
    val contactsState: ContactsState = ContactsState.Loading,
    val activeProfileTab: ProfileType = ProfileType.WORK
)


@Immutable
data class ContactSummaryUiState(
    val fullName: String,
    val metAt: String?,
    val timeAgo: String,
    val profilePicture: String?,
    val id: Long,
    val tag: String?
)

@Immutable
data class EventsUiState(
    val id: Long,
    val eventName: String,
    val eventDate: String,
    val numberOfConnections: String
)

@Immutable
data class ProfileUiState(
    val fullName: String,
    val organization: String?,
    val role: String?,
    val userNameFromFullName: String,
    val profilePictureUri: String?
)

sealed interface ProfileState {
    data object Loading : ProfileState
    data class Success(val profileUiState: ProfileUiState) : ProfileState
    data class Error(val message: String) : ProfileState
}

sealed interface EventsState {
    data object Loading : EventsState
    data class Success(val eventsUiState: List<EventsUiState>) : EventsState
    data class Error(val message: String) : EventsState
}

sealed interface ContactsState {
    data object Loading : ContactsState
    data class Success(val contactsUiState: List<ContactSummaryUiState>) : ContactsState
    data class Error(val message: String) : ContactsState
}

// Mappers simplified
fun ContactSummary.toUiState() = ContactSummaryUiState(fullName, metAt, timeAgo, profilePicture, id, tag)
fun EventsSummary.toUi() = EventsUiState(eventId, eventName, eventDate, numberOfContacts.toString())

fun ProfileDataModel.toHomeProfileDisplayUiModel() = ProfileUiState(
    fullName = this.fullName,
    organization = this.organization,
    role = this.role,
    userNameFromFullName = this.userName,
    profilePictureUri = this.profilePicture
)

