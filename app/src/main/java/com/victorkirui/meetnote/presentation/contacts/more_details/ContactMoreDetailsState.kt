package com.victorkirui.meetnote.presentation.contacts.more_details

import com.victorkirui.meetnote.domain.model.ContactSummaryForAddContactScreenModel
import com.victorkirui.meetnote.presentation.state.ProfileType
import com.victorkirui.meetnote.presentation.state.SocialLinkState

// Keep this flat and simple for real-time text input updates
data class ContactMoreDetailsUiState(
    val id: Long = 0,
    val firstName: String = "",
    val lastName: String = "",
    val fullName: String = "",
    val tag: ProfileType = ProfileType.WORK,
    val phoneNumber: String? = null,
    val profilePicture: String? = null,
    val email: String? = null,
    val organization: String = "",
    val role: String = "",
    val location: String = "",
    val notes: String = "",
    val socialLinks: List<SocialLinkState> = listOf(
        SocialLinkState(platform = "LinkedIn"),
        SocialLinkState(platform = "Instagram"),
        SocialLinkState(platform = "Website")
    )
)

// The lifecycle state handles structural layout modes
sealed interface ContactMoreDetailsStatus {
    data object Fetching : ContactMoreDetailsStatus
    data object Idle : ContactMoreDetailsStatus
    data object Saving : ContactMoreDetailsStatus
    data object Saved : ContactMoreDetailsStatus
    data class Error(val message: String) : ContactMoreDetailsStatus
}

fun ContactSummaryForAddContactScreenModel.toUiState(): ContactMoreDetailsUiState = ContactMoreDetailsUiState(
    id = this.id,
    fullName = this.fullName,
    firstName = this.fullName.split(" ").firstOrNull() ?: "",
    lastName = this.fullName.split(" ").drop(1).joinToString(" "),
    tag = if(this.tag == ProfileType.WORK.name) ProfileType.WORK else ProfileType.SOCIAL,
    email = this.emailAddress,
    phoneNumber = this.phoneNumber,
    profilePicture = this.profilePictureUri
)

sealed interface ContactMoreDetailsUiEffect{
    data class ShowToast(val message: String): ContactMoreDetailsUiEffect
}
