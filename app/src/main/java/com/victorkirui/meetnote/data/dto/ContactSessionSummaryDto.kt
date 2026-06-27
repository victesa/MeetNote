package com.victorkirui.meetnote.data.dto

data class ContactSessionSummaryDto(
    val id: Long,
    val name: String,
    val metAt: String?,
    val metOn: Long?,
    val profilePictureUri: String?,
    val tag: String?
)
