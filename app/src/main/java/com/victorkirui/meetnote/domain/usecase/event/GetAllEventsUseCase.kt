package com.victorkirui.meetnote.domain.usecase.event

import com.victorkirui.meetnote.domain.model.EventsSummary
import com.victorkirui.meetnote.domain.repository.EventsRepository
import kotlinx.coroutines.flow.Flow

class GetAllEventsUseCase(
    private val repository: EventsRepository
) {
    operator fun invoke(): Flow<Result<List<EventsSummary>>> {
        return repository.getAllEventsWithSessions()
    }
}
