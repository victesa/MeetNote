package com.victorkirui.meetnote.domain.model

data class AddEventModel(
    val id: Long = 0,
    val name: String,
    val date: String,
    val location: String?,
    val eventType: String,
    val notes: String?
)
