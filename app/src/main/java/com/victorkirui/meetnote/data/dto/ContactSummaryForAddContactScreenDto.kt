package com.victorkirui.meetnote.data.dto

import com.victorkirui.meetnote.domain.model.ContactSummaryForAddContactScreenModel

data class ContactSummaryForAddContactScreenDto(
    val id: Long,
    val fullName: String = "",
    val profilePictureUri: String? = null,
    val tag: String = "",
    val phoneNumber: String? = null,
    val emailAddress: String? = null
)

fun ContactSummaryForAddContactScreenDto.toDomainModel(): ContactSummaryForAddContactScreenModel = ContactSummaryForAddContactScreenModel(
    id = this.id,
    fullName = this.fullName,
    emailAddress = this.emailAddress,
    phoneNumber = this.phoneNumber,
    profilePictureUri = this.profilePictureUri,
    tag = this.tag
)
