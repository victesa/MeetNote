package com.victorkirui.meetnote.data.mapper

import com.victorkirui.meetnote.data.local.entity.EventWithSessions
import com.victorkirui.meetnote.domain.model.EventsSummary

fun EventWithSessions.toDomainEventWithSessionModel(): EventsSummary = EventsSummary(
    eventId = this.event.id,
    eventDate = this.event.eventDate,
    eventName = this.event.name,
    location = this.event.location,
    eventType = this.event.eventType,
    numberOfContacts = this.sessions.size
)
