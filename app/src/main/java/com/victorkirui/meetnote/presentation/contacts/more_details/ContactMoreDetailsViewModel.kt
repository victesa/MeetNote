package com.victorkirui.meetnote.presentation.contacts.more_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victorkirui.meetnote.domain.model.ContactWithDetailsModel
import com.victorkirui.meetnote.domain.model.SocialLinkModel
import com.victorkirui.meetnote.domain.usecase.contact.GetContactSummaryUseCase
import com.victorkirui.meetnote.domain.usecase.contact.UpdateContactMoreDetailsUseCase
import com.victorkirui.meetnote.presentation.contacts.add.AddContactEvents
import com.victorkirui.meetnote.presentation.state.ProfileType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContactMoreDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val getContactSummaryUseCase: GetContactSummaryUseCase,
    private val updateContactMoreDetailsUseCase: UpdateContactMoreDetailsUseCase
) : ViewModel() {

    private val contactId: Long = savedStateHandle["contact_id"]
        ?: throw IllegalArgumentException("Contact Id missing")

    // Stream 1: Tracks editable form inputs
    private val _formInput = MutableStateFlow(ContactMoreDetailsUiState())
    val formInput = _formInput.asStateFlow()

    // Stream 2: Tracks screen/network lifecycle operations
    private val _submissionState = MutableStateFlow<ContactMoreDetailsStatus>(ContactMoreDetailsStatus.Fetching)
    val submissionState = _submissionState.asStateFlow()

    private val _uiEvents = Channel<AddContactEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    init {
        fetchInitialContactSummary()
    }

    private fun fetchInitialContactSummary() {
        viewModelScope.launch {
            getContactSummaryUseCase(contactId)
                .collect { result ->
                    result.fold(
                        onSuccess = { summary ->
                            _formInput.value = summary.toUiState()
                            _submissionState.value = ContactMoreDetailsStatus.Idle
                        },
                        onFailure = { throwable ->
                            val msg = if (throwable is NoSuchElementException) "Saved info not found" else "An unexpected error occurred"
                            _submissionState.value = ContactMoreDetailsStatus.Error(msg)
                        }
                    )
                }
        }
    }

    // Direct, state-safe property updates for character inputs
    fun onFirstNameChange(newValue: String) = _formInput.update { it.copy(firstName = newValue) }
    fun onLastNameChange(newValue: String) = _formInput.update { it.copy(lastName = newValue) }
    fun onEmailChange(newValue: String) = _formInput.update { it.copy(email = newValue) }
    fun onPhoneNumberChange(newValue: String) = _formInput.update { it.copy(phoneNumber = newValue) }
    fun onTagChange(newValue: ProfileType) = _formInput.update { it.copy(tag = newValue) }
    fun onOrganizationChange(newValue: String) = _formInput.update { it.copy(organization = newValue) }
    fun onRoleChange(newValue: String) = _formInput.update { it.copy(role = newValue) }
    fun onLocationChange(newValue: String) = _formInput.update { it.copy(location = newValue) }
    fun onNotesChange(newValue: String) = _formInput.update { it.copy(notes = newValue) }

    fun onSocialLinkChange(index: Int, url: String) {
        _formInput.update { state ->
            state.copy(
                socialLinks = state.socialLinks.mapIndexed { i, link ->
                    if (i == index) link.copy(url = url) else link
                }
            )
        }
    }

    fun onSaveContactDetails() {
        val state = _formInput.value
        viewModelScope.launch {
            _submissionState.value = ContactMoreDetailsStatus.Saving

            val result = updateContactMoreDetailsUseCase(
                ContactWithDetailsModel(
                    id = contactId,
                    firstName = state.firstName,
                    lastName = state.lastName,
                    fullName = "${state.firstName} ${state.lastName}".trim(),
                    emailAddress = state.email,
                    phoneNumber = state.phoneNumber,
                    organization = state.organization,
                    role = state.role,
                    location = state.location,
                    notes = state.notes,
                    socialLinks = state.socialLinks
                        .filter { it.url.isNotBlank() }
                        .map { 
                            SocialLinkModel(platform = it.platform, url = it.url)
                        },
                    profilePictureUri = state.profilePicture,
                    tag = state.tag.name,
                    metOn = System.currentTimeMillis()
                )
            )

            result.fold(
                onSuccess = { 
                    _submissionState.value = ContactMoreDetailsStatus.Saved 
                    _uiEvents.send(AddContactEvents.ShowConfirmationSheet(
                        id = contactId,
                        firstName = state.firstName,
                        lastName = state.lastName,
                        profilePictureUri = state.profilePicture,
                        metAt = "", // Should come from DB
                        tag = state.tag
                    ))
                },
                onFailure = { _submissionState.value = ContactMoreDetailsStatus.Error(it.message ?: "Failed to save") }
            )
        }
    }
}
