package com.victorkirui.meetnote.presentation.contacts.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victorkirui.meetnote.domain.model.AddContactModel
import com.victorkirui.meetnote.domain.model.EventsSummary
import com.victorkirui.meetnote.domain.usecase.contact.SaveContactUseCase
import com.victorkirui.meetnote.domain.usecase.event.GetAllEventsUseCase
import com.victorkirui.meetnote.domain.usecase.SaveProfilePictureUseCase
import com.victorkirui.meetnote.domain.util.qr.QRCodeParser
import com.victorkirui.meetnote.domain.util.qr.ScanResultManager
import com.victorkirui.meetnote.presentation.state.ProfileType
import com.victorkirui.meetnote.presentation.state.SocialLinkState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddContactViewModel(
    private val saveContactUseCase: SaveContactUseCase,
    private val saveProfilePictureUseCase: SaveProfilePictureUseCase,
    private val getAllEventsUseCase: GetAllEventsUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _formInput = MutableStateFlow(AddContactUiState())
    val formInput = _formInput.asStateFlow()

    private val _formSubmittingState: MutableStateFlow<AddContactFormSubmissionState> = MutableStateFlow(AddContactFormSubmissionState.Idle)
    val formSubmittingState = _formSubmittingState.asStateFlow()

    private val _uiEvents = Channel<AddContactEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _profilePictureUri: MutableStateFlow<String?> = MutableStateFlow(null)
    val profilePictureUri = _profilePictureUri.asStateFlow()

    private val _profilePictureUploadState: MutableStateFlow<AddContactFormSubmissionState> = MutableStateFlow(AddContactFormSubmissionState.Idle)
    val profilePictureUploadState = _profilePictureUploadState.asStateFlow()

    private val _events = MutableStateFlow<List<EventsSummary>>(emptyList())
    val events = _events.asStateFlow()

    init {
        fetchEvents()
        checkForQrData()
    }

    private fun checkForQrData() {
        android.util.Log.d("DEBUG_SCAN", "Step 4: ViewModel init. SavedStateHandle keys: ${savedStateHandle.keys()}")
        
        // Strategy 1: Check SavedStateHandle (Standard Nav way)
        val navData: String? = savedStateHandle["qr_data"]
        if (navData != null) {
            android.util.Log.d("DEBUG_SCAN", "Step 5a: Found data in SavedStateHandle: $navData")
            applyParsedData(navData)
            savedStateHandle["qr_data"] = null
        }

        // Strategy 2: Check ScanResultManager (Reliable Singleton way)
        ScanResultManager.pendingResult
            .filterNotNull()
            .onEach { data ->
                android.util.Log.d("DEBUG_SCAN", "Step 5b: Found data in ScanResultManager: $data")
                applyParsedData(data)
                ScanResultManager.clear()
            }
            .launchIn(viewModelScope)
    }

    private fun applyParsedData(data: String) {
        android.util.Log.d("DEBUG_SCAN", "Step 6: Parsing data: $data")
        val parsed = QRCodeParser.parse(data)
        android.util.Log.d("DEBUG_SCAN", "Step 7: Parsed results: name=${parsed.fullName}, email=${parsed.email}, phone=${parsed.phone}")
        
        _formInput.update { it.copy(
            firstName = parsed.fullName.split(" ").firstOrNull() ?: "",
            lastName = parsed.fullName.split(" ").drop(1).joinToString(" "),
            email = parsed.email ?: "",
            phoneNumber = parsed.phone ?: "",
            organization = parsed.organization ?: "",
            role = parsed.role ?: "",
            selectedTag = parsed.profileType,
            socialLinks = if (parsed.socialLinks.isNotEmpty()) {
                parsed.socialLinks.map { SocialLinkState(platform = it.first, url = it.second) }
            } else it.socialLinks
        ) }
        android.util.Log.d("DEBUG_SCAN", "Step 8: UI State updated with parsed data")
    }

    private fun fetchEvents() {
        viewModelScope.launch {
            getAllEventsUseCase().collect { result ->
                result.onSuccess { _events.value = it }
            }
        }
    }

    fun onFirstNameChange(value: String) = _formInput.update { it.copy(firstName = value) }
    fun onLastNameChange(value: String) = _formInput.update { it.copy(lastName = value) }
    fun onTagChange(value: ProfileType) = _formInput.update { it.copy(selectedTag = value) }
    fun onEmailChange(value: String) = _formInput.update { it.copy(email = value) }
    fun onPhoneNumberChange(value: String) = _formInput.update { it.copy(phoneNumber = value) }
    fun onMetAtChange(value: String) = _formInput.update { it.copy(metAt = value, selectedEventId = null) }
    fun onEventSelected(id: Long, name: String) {
        val eventType = events.value.find { it.eventId == id }?.eventType
        val tag = if (eventType == "WORK") ProfileType.WORK else if (eventType == "SOCIAL") ProfileType.SOCIAL else _formInput.value.selectedTag
        
        _formInput.update { it.copy(
            metAt = name, 
            selectedEventId = id,
            selectedTag = tag
        ) }
    }
    fun onContactMethodChange(value: ContactMethod) = _formInput.update { it.copy(selectedContactMethod = value) }

    fun onProfilePictureSelected(tempUriString: String) {
        viewModelScope.launch {
            _profilePictureUploadState.value = AddContactFormSubmissionState.Saving
            val result = saveProfilePictureUseCase(tempUriString)
            result.onSuccess { path ->
                _profilePictureUploadState.value = AddContactFormSubmissionState.Success
                _profilePictureUri.update { path }
            }
            result.onFailure {
                _profilePictureUploadState.update { AddContactFormSubmissionState.Error }
                _uiEvents.send(AddContactEvents.ShowToast(errorMessage = "Image Failed to Upload. Please Try Again"))
            }
        }
    }

    fun saveContact(saveOption: SaveOption) {
        val state = _formInput.value
        val fullName = "${state.firstName} ${state.lastName}".trim()
        if (fullName.isBlank()) {
            _formInput.update { it.copy(namesError = true) }
            _formSubmittingState.update { AddContactFormSubmissionState.Error }
            viewModelScope.launch {
                _uiEvents.send(AddContactEvents.ShowToast(errorMessage = "Name cannot be empty"))
            }
            return
        }

        _formInput.update { it.copy(namesError = false) }
        _formSubmittingState.update { AddContactFormSubmissionState.Saving }

        viewModelScope.launch {
            val result = saveContactUseCase(
                AddContactModel(
                    firstName = state.firstName,
                    lastName = state.lastName,
                    fullName = fullName,
                    email = state.email.ifBlank { null },
                    phoneNumber = state.phoneNumber.ifBlank { null },
                    metAt = state.metAt,
                    eventId = state.selectedEventId,
                    profilePictureUri = profilePictureUri.value,
                    tag = state.selectedTag.name,
                    metOn = System.currentTimeMillis(),
                    organization = null,
                    role = null,
                    location = null,
                    notes = null,
                    socialLinks = emptyList()
                )
            )

            result.onSuccess { id ->
                _formSubmittingState.update { AddContactFormSubmissionState.Success }
                if (saveOption == SaveOption.SAVEONLY) {
                    _uiEvents.send(AddContactEvents.ShowConfirmationSheet(
                        id = id,
                        firstName = formInput.value.firstName,
                        lastName = formInput.value.lastName,
                        profilePictureUri = profilePictureUri.value,
                        metAt = formInput.value.metAt,
                        tag = formInput.value.selectedTag
                    ))
                } else {
                    _uiEvents.send(AddContactEvents.NavigateToAddMoreDetailsScreen(id))
                }
            }
            result.onFailure { error ->
                _formSubmittingState.update { AddContactFormSubmissionState.Error }
                _uiEvents.send(AddContactEvents.ShowToast(errorMessage = error.message ?: "Failed to save contact"))
            }
        }
    }
}
