package com.victorkirui.meetnote.domain.model

data class ContactSummaryForAddContactScreenModel(
    val id: Long,
    val fullName: String = "",
    val profilePictureUri: String? = null,
    val tag: String = "",
    val phoneNumber: String? = null,
    val emailAddress: String? = null
)

