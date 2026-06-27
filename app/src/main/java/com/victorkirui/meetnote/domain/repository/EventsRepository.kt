package com.victorkirui.meetnote.domain.repository

import com.victorkirui.meetnote.domain.model.AddEventModel
import com.victorkirui.meetnote.domain.model.EventsSummary
import kotlinx.coroutines.flow.Flow

interface EventsRepository {
    fun getAllEventsWithSessions(): Flow<Result<List<EventsSummary>>>
    suspend fun saveEvent(event: AddEventModel): Result<Long>
    fun getEventDetails(id: Long): Flow<Result<com.victorkirui.meetnote.domain.model.EventDetailsModel>>
    suspend fun deleteEvent(id: Long, deleteContacts: Boolean): Result<Unit>
}