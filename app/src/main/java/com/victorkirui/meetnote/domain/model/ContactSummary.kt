package com.victorkirui.meetnote.domain.model

data class ContactSummary(
    val id: Long,
    val fullName: String,
    val metAt: String?,
    val timeAgo: String,
    val profilePicture: String?,
    val tag: String?
)
