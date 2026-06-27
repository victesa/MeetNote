package com.victorkirui.meetnote.domain.model

data class EventsSummary(
    val eventId: Long,
    val eventName: String,
    val eventDate: String, // Keep raw date from DB, formatting handled in UI
    val location: String?,
    val eventType: String,
    val numberOfContacts: Int
)
