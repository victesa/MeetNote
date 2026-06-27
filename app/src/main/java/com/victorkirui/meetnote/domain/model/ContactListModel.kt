package com.victorkirui.meetnote.domain.model

data class ContactListModel(
    val id: Long = 0,
    val fullName: String,
    val profilePictureUri: String?,
    val role: String?,
    val organization: String?,
    val metAt: String?,
    val tag: String?
)
