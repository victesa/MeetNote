package com.victorkirui.meetnote.presentation.contacts.add

import com.victorkirui.meetnote.presentation.state.ProfileType
import com.victorkirui.meetnote.presentation.state.SocialLinkState

data class AddContactUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val metAt: String = "",
    val selectedEventId: Long? = null,
    val organization: String = "",
    val role: String = "",
    val location: String = "",
    val notes: String = "",
    val socialLinks: List<SocialLinkState> = listOf(
        SocialLinkState(platform = "LinkedIn"),
        SocialLinkState(platform = "Instagram"),
        SocialLinkState(platform = "Website")
    ),
    val selectedTag: ProfileType = ProfileType.WORK,
    val selectedContactMethod: ContactMethod = ContactMethod.PHONE,

    val namesError: Boolean = false,
    val contactError: Boolean = false,
){
    val canSaveContact: Boolean
        get() = (firstName.isNotEmpty() || lastName.isNotEmpty()) &&
                (email.isNotEmpty() || phoneNumber.isNotEmpty())
}

sealed interface AddContactFormSubmissionState{
    data object Idle: AddContactFormSubmissionState

    data object Saving: AddContactFormSubmissionState

    data object Success: AddContactFormSubmissionState

    data object Error: AddContactFormSubmissionState
}


sealed interface AddContactEvents{
    data class ShowConfirmationSheet(val id: Long,
                                     val firstName: String,
                                     val lastName: String,
                                     val profilePictureUri: String?,
                                     val metAt: String,
                                     val tag: ProfileType): AddContactEvents

    data class ShowToast(val errorMessage: String): AddContactEvents

    data class NavigateToAddMoreDetailsScreen(val id: Long): AddContactEvents
}

enum class SaveOption{
    SAVEONLY, SAVEANDADDMOREDETAILS
}

enum class ContactMethod {
    PHONE, EMAIL
}
