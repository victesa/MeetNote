package com.victorkirui.meetnote.domain.model

data class EventDetailsModel(
    val id: Long,
    val name: String,
    val date: String,
    val location: String?,
    val eventType: String,
    val notes: String?,
    val contactsExchanged: Int,
    val contactsMet: List<ContactMetSummary>
)

data class ContactMetSummary(
    val id: Long,
    val fullName: String,
    val role: String?,
    val organization: String?,
    val profilePictureUri: String?,
    val tag: String
)
