package com.victorkirui.meetnote.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victorkirui.meetnote.domain.usecase.SaveMySocialProfileUseCase
import com.victorkirui.meetnote.domain.usecase.SaveProfilePictureUseCase
import com.victorkirui.meetnote.domain.util.ValidationError
import com.victorkirui.meetnote.domain.util.ValidationException
import com.victorkirui.meetnote.presentation.state.SocialLinkState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileSetupSocialViewModel(
    private val saveProfilePictureUseCase: SaveProfilePictureUseCase,
    private val saveMySocialProfileUseCase: SaveMySocialProfileUseCase,
    private val myProfileRepository: com.victorkirui.meetnote.domain.repository.MyProfileRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val _profilePictureUploadState: MutableStateFlow<ProfilePictureUploadState> = MutableStateFlow(ProfilePictureUploadState.Idle)
    val profilePictureUploadState = _profilePictureUploadState.asStateFlow()

    private val _uiEffects = Channel<ProfileScreenEvent>()
    val uiEffects = _uiEffects.receiveAsFlow()

    init {
        fetchExistingProfile()
    }

    private fun fetchExistingProfile() {
        viewModelScope.launch {
            myProfileRepository.getSocialProfile().collect { result ->
                result.onSuccess { domain ->
                    _uiState.update { it.fromDomain(domain) }
                }
            }
        }
    }

    fun onProfilePictureSelected(tempUriString: String) {
        viewModelScope.launch {
            _profilePictureUploadState.value = ProfilePictureUploadState.Uploading
            val result = saveProfilePictureUseCase(tempUriString)
            result.onSuccess { path ->
                _profilePictureUploadState.value = ProfilePictureUploadState.Success(path)
                _uiState.update { it.copy(profilePictureUri = path) }
            }
            result.onFailure {
                _profilePictureUploadState.value = ProfilePictureUploadState.Error("Image Failed to Upload. Please Try Again")
            }
        }
    }

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(fullName = name, fullNameHasError = false) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, emailHasError = false) }
    }

    fun onPhoneNumberChanged(phoneNumber: String) {
        _uiState.update { it.copy(phoneNumber = phoneNumber, phoneNumberHasError = false) }
    }

    fun onOrganizationChanged(organization: String) {
        _uiState.update { it.copy(organization = organization) }
    }

    fun onRoleChanged(position: String) {
        _uiState.update { it.copy(role = position) }
    }

    fun onSocialLinkChanged(indexPosition: Int, socialLink: SocialLinkState) {
        _uiState.update { state ->
            state.copy(
                socialLinks = state.socialLinks.mapIndexed { index, currentItem ->
                    if (index == indexPosition) socialLink else currentItem
                }
            )
        }
    }

    fun onAddSocialLink() {
        _uiState.update { it.copy(socialLinks = it.socialLinks + SocialLinkState()) }
    }

    fun onDeleteSocialLink(indexPos: Int) {
        _uiState.update { state ->
            state.
            copy(socialLinks = state.socialLinks.filterIndexed { index, _ -> index != indexPos })
        }
    }

    fun onSaveSocialProfile() {
        val currentState = _uiState.value
        viewModelScope.launch {
            val result = saveMySocialProfileUseCase(currentState.toDomain())

            result.onFailure { exception ->
                // 1. Resolve the exact text intended for the Toast message
                val toastMessage = if (exception is ValidationException) {
                    when (exception.errorType) {
                        ValidationError.BothInvalid -> "Both email and phone number are invalid."
                        ValidationError.InvalidPhoneNumber -> "Phone number format is incorrect."
                        ValidationError.InvalidEmail -> "Email format is incorrect."
                        else -> exception.message ?: "Validation failed. Please try again."
                    }
                } else {
                    exception.message ?: "An unknown network error occurred."
                }

                // 2. Update the persistent UI state *only* for the red input field error borders
                if (exception is ValidationException) {
                    _uiState.update { state ->
                        when (exception.errorType) {
                            ValidationError.BothInvalid -> state.copy(
                                emailHasError = true,
                                phoneNumberHasError = true
                            )
                            ValidationError.InvalidPhoneNumber -> state.copy(
                                phoneNumberHasError = true
                            )
                            ValidationError.InvalidEmail -> state.copy(
                                emailHasError = true
                            )
                            else -> state
                        }
                    }
                }

                // 3. Fire the Toast instantly through the side-effect channel
                _uiEffects.send(ProfileScreenEvent.ShowToast(toastMessage))
            }

            result.onSuccess {
                _uiEffects.send(ProfileScreenEvent.NavigateForward)
            }
        }
    }
}