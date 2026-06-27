package com.victorkirui.meetnote.presentation.scan

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victorkirui.meetnote.domain.model.AddContactModel
import com.victorkirui.meetnote.domain.model.EventsSummary
import com.victorkirui.meetnote.domain.model.SocialLinkModel
import com.victorkirui.meetnote.domain.usecase.contact.SaveContactUseCase
import com.victorkirui.meetnote.domain.usecase.event.GetAllEventsUseCase
import com.victorkirui.meetnote.domain.util.qr.QRCodeParser
import com.victorkirui.meetnote.domain.util.qr.ScanResultManager
import com.victorkirui.meetnote.presentation.contacts.add.AddContactEvents
import com.victorkirui.meetnote.presentation.state.ProfileType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ScannedContactViewModel(
    private val saveContactUseCase: SaveContactUseCase,
    private val getAllEventsUseCase: GetAllEventsUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannedContactUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = Channel<AddContactEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _uiEffects = Channel<ScannedContactEffect>()
    val uiEffects = _uiEffects.receiveAsFlow()

    private val _events = MutableStateFlow<List<EventsSummary>>(emptyList())
    val events = _events.asStateFlow()

    init {
        fetchEvents()
        checkForQrData()
    }

    private fun checkForQrData() {
        // Strategy 1: Check SavedStateHandle
        val navData: String? = savedStateHandle["qr_data"]
        if (navData != null) {
            applyParsedData(navData)
            savedStateHandle["qr_data"] = null
        }

        // Strategy 2: Check ScanResultManager
        ScanResultManager.pendingResult
            .filterNotNull()
            .onEach { data ->
                applyParsedData(data)
                ScanResultManager.clear()
            }
            .launchIn(viewModelScope)
    }

    private fun applyParsedData(data: String) {
        val parsed = QRCodeParser.parse(data)
        if (!parsed.isValidContact) {
            ScanResultManager.clear() // Ensure it's cleared if invalid
            _uiEffects.trySend(ScannedContactEffect.NavigateToError)
            return
        }

        _uiState.update { it.copy(
            fullName = parsed.fullName,
            email = parsed.email,
            phoneNumber = parsed.phone,
            organization = parsed.organization,
            role = parsed.role,
            tag = parsed.profileType,
            socialLinks = parsed.socialLinks.map { link -> Pair(link.first, link.second) }
        ) }
    }

    private fun fetchEvents() {
        viewModelScope.launch {
            getAllEventsUseCase().collect { result ->
                result.onSuccess { _events.value = it }
            }
        }
    }

    fun onEventSelected(id: Long, name: String) {
        val eventType = events.value.find { it.eventId == id }?.eventType
        val tag = if (eventType == "WORK") ProfileType.WORK else if (eventType == "SOCIAL") ProfileType.SOCIAL else _uiState.value.tag
        
        _uiState.update { it.copy(
            metAt = name, 
            selectedEventId = id,
            tag = tag
        ) }
    }

    fun saveContact() {
        val state = _uiState.value
        viewModelScope.launch {
            val result = saveContactUseCase(
                AddContactModel(
                    firstName = state.fullName.split(" ").firstOrNull() ?: "",
                    lastName = state.fullName.split(" ").drop(1).joinToString(" "),
                    fullName = state.fullName,
                    email = state.email,
                    phoneNumber = state.phoneNumber,
                    metAt = state.metAt,
                    eventId = state.selectedEventId,
                    profilePictureUri = null,
                    tag = state.tag.name,
                    metOn = System.currentTimeMillis(),
                    organization = state.organization,
                    role = state.role,
                    socialLinks = state.socialLinks.map { SocialLinkModel(platform = it.first, url = it.second) }
                )
            )

            result.onSuccess { id ->
                _uiEvents.send(AddContactEvents.ShowConfirmationSheet(
                    id = id,
                    firstName = state.fullName.split(" ").firstOrNull() ?: "",
                    lastName = state.fullName.split(" ").drop(1).joinToString(" "),
                    profilePictureUri = null,
                    metAt = state.metAt,
                    tag = state.tag
                ))
            }
            result.onFailure { error ->
                _uiEvents.send(AddContactEvents.ShowToast(errorMessage = error.message ?: "Failed to save contact"))
            }
        }
    }
}

sealed interface ScannedContactEffect {
    data object NavigateToError : ScannedContactEffect
}

data class ScannedContactUiState(
    val fullName: String = "",
    val email: String? = null,
    val phoneNumber: String? = null,
    val organization: String? = null,
    val role: String? = null,
    val tag: ProfileType = ProfileType.WORK,
    val metAt: String = "",
    val selectedEventId: Long? = null,
    val socialLinks: List<Pair<String, String>> = emptyList()
)
