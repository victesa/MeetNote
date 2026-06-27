package com.victorkirui.meetnote.presentation.profile

import com.victorkirui.meetnote.domain.model.ProfileDataModel
import com.victorkirui.meetnote.presentation.state.SocialLinkState
import com.victorkirui.meetnote.presentation.state.toDomain

data class ProfileUiState(
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val organization: String = "",
    val role: String = "",
    val socialLinks: List<SocialLinkState> = emptyList(),
    val profilePictureUri: String = "",

    // Field Error Visual Indicators
    val fullNameHasError: Boolean = false,
    val emailHasError: Boolean = false,
    val phoneNumberHasError: Boolean = false,
    val profilePictureHasError: Boolean = false,
){
    val canSaveForm: Boolean
        get() {
            val isNameEntered = fullName.isNotBlank()

            val hasEmail = email.isNotBlank()
            val hasPhone = phoneNumber.isNotBlank()

            val hasAtLeastOneContact = hasEmail || hasPhone

            return isNameEntered && hasAtLeastOneContact
        }
}

fun ProfileUiState.toDomain(): ProfileDataModel = ProfileDataModel(
    fullName = this.fullName,
    email = this.email.ifEmpty { null },
    phoneNumber = this.phoneNumber.ifEmpty { null },
    organization = this.organization.ifEmpty { null },
    role = this.role.ifEmpty { null },
    socialLinks = this.socialLinks.map { it.toDomain() },
    profilePicture = this.profilePictureUri.ifEmpty { null }
)

fun ProfileUiState.fromDomain(domain: ProfileDataModel): ProfileUiState = this.copy(
    fullName = domain.fullName,
    email = domain.email ?: "",
    phoneNumber = domain.phoneNumber ?: "",
    organization = domain.organization ?: "",
    role = domain.role ?: "",
    socialLinks = domain.socialLinks.map { SocialLinkState(platform = it.platform, url = it.url) },
    profilePictureUri = domain.profilePicture ?: ""
)


sealed interface ProfilePictureUploadState {
    data object Idle : ProfilePictureUploadState
    data object Uploading : ProfilePictureUploadState
    data class Success(val profilePictureUri: String) : ProfilePictureUploadState
    data class Error(val message: String) : ProfilePictureUploadState
}

sealed interface ProfileScreenEvent {
    data object NavigateForward : ProfileScreenEvent
    data class ShowToast(val message: String) : ProfileScreenEvent
}
