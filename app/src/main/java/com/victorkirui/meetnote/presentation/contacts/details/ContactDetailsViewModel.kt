package com.victorkirui.meetnote.presentation.contacts.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victorkirui.meetnote.domain.repository.ContactsRepository
import com.victorkirui.meetnote.presentation.state.ProfileType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ContactDetailsViewModel(
    private val contactsRepository: ContactsRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val contactId: Long = savedStateHandle["contact_id"] ?: throw IllegalArgumentException("Contact Id required")

    val contactTag: ProfileType = savedStateHandle.get<String>("contact_tag")?.let { 
        ProfileType.valueOf(it) 
    } ?: throw IllegalArgumentException("Contact Tag Required")

    private val _uiEvents = Channel<ContactDetailsUiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    val uiState: StateFlow<ContactDetailsScreenState> = contactsRepository.getContactWithDetails(contactId).map { result ->
        result.fold(
            onSuccess = {
                ContactDetailsScreenState.Success(contactDetails = it.toUi())
            },
            onFailure = {
                ContactDetailsScreenState.Error(
                    errorMessage = it.message ?: "An unexpected Error Occurred",
                    profileType = contactTag
                )
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ContactDetailsScreenState.Loading(profileType = contactTag)
    )

    fun deleteContact() {
        viewModelScope.launch {
            contactsRepository.deleteContact(contactId)
            _uiEvents.send(ContactDetailsUiEvent.NavigateBack)
        }
    }

    fun shareContact() {
        val currentState = uiState.value
        if (currentState is ContactDetailsScreenState.Success) {
            val details = currentState.contactDetails
            val vCard = buildString {
                append("BEGIN:VCARD\n")
                append("VERSION:3.0\n")
                append("FN:${details.fullName}\n")
                details.phoneNumber?.let { append("TEL:${it}\n") }
                details.emailAddress?.let { append("EMAIL:${it}\n") }
                details.organization?.let { append("ORG:${it}\n") }
                details.role?.let { append("TITLE:${it}\n") }
                append("END:VCARD")
            }
            viewModelScope.launch {
                _uiEvents.send(ContactDetailsUiEvent.ShareContact(vCard))
            }
        }
    }
}

sealed interface ContactDetailsUiEvent {
    data object NavigateBack : ContactDetailsUiEvent
    data class ShareContact(val vCard: String) : ContactDetailsUiEvent
}
