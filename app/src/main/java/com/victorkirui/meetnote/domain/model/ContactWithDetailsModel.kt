package com.victorkirui.meetnote.domain.model

import com.victorkirui.meetnote.domain.util.GenerateUserName

data class ContactWithDetailsModel(
    val id: Long,
    val firstName: String = "",
    val lastName: String = "",
    val fullName: String,
    val emailAddress: String? = null,
    val phoneNumber: String? = null,
    val organization: String? = null,
    val role: String? = null,
    val location: String? = null,
    val socialLinks: List<SocialLinkModel> = emptyList(),
    val metAt: String? = null,
    val metOn: Long,
    val notes: String? = null,
    val profilePictureUri: String? = null,
    val tag: String,
    val userName: String = GenerateUserName.generateUserNameForHomeProfileDisplayUiModel(fullName)
)
