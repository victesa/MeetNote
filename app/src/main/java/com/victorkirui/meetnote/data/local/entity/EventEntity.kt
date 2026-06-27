package com.victorkirui.meetnote.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,
    val eventDate: String,
    val location: String? = null,
    val eventType: String,
    val notes: String? = null
)
