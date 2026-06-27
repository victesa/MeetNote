package com.victorkirui.meetnote.domain.model

data class AddContactModel(
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val email: String?,
    val phoneNumber: String?,
    val tag: String,
    val metAt: String,
    val eventId: Long? = null,
    val profilePictureUri: String?,
    val organization: String? = null,
    val role: String? = null,
    val location: String? = null,
    val notes: String? = null,
    val socialLinks: List<SocialLinkModel> = emptyList(),
    val metOn: Long = System.currentTimeMillis()
)
