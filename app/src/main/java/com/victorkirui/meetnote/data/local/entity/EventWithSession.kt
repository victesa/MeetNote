package com.victorkirui.meetnote.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class EventWithSessions(
    @Embedded val event: EventEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "eventId"
    )
    val sessions: List<ContactEntity>
)
