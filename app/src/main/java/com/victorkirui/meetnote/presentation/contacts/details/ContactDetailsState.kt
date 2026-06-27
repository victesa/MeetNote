package com.victorkirui.meetnote.presentation.contacts.details

import com.victorkirui.meetnote.domain.model.ContactWithDetailsModel
import com.victorkirui.meetnote.presentation.state.ProfileType
import com.victorkirui.meetnote.presentation.state.SocialLinkState
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

sealed interface ContactDetailsScreenState {
    data class Loading(val profileType: ProfileType) : ContactDetailsScreenState
    data class Success(val contactDetails: ContactDetailsScreenUiState) : ContactDetailsScreenState
    data class Error(val errorMessage: String, val profileType: ProfileType) : ContactDetailsScreenState
}

data class ContactDetailsScreenUiState(
    val id: Long,
    val fullName: String,
    val emailAddress: String? = null,
    val phoneNumber: String? = null,
    val organization: String? = null,
    val role: String? = null,
    val location: String? = null,
    val socialLinks: List<SocialLinkState>,
    val metAt: String? = null,
    val metOn: String,
    val notes: String? = null,
    val profilePictureUri: String? = null,
    val profileType: ProfileType, // 2. Enforced type-safety over raw Strings
    val userName: String
)

fun ContactWithDetailsModel.toUi(): ContactDetailsScreenUiState {
    val resolvedType = runCatching { ProfileType.valueOf(this.tag.uppercase()) }
        .getOrDefault(ProfileType.WORK)

    return ContactDetailsScreenUiState(
        id = this.id,
        fullName = this.fullName,
        emailAddress = this.emailAddress,
        phoneNumber = this.phoneNumber,
        organization = this.organization,
        role = this.role,
        location = this.location,
        metAt = this.metAt,
        metOn = formatTimestamp(this.metOn),
        notes = this.notes,
        profilePictureUri = this.profilePictureUri,
        profileType = resolvedType,
        socialLinks = this.socialLinks.map { SocialLinkState(url = it.url, platform = it.platform) },
        userName = this.userName
    )
}

fun formatTimestamp(timestampInMillis: Long, locale: Locale = Locale.getDefault()): String {
    val instant = Instant.ofEpochMilli(timestampInMillis)
    val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDate()
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", locale)
    return localDateTime.format(formatter)
}