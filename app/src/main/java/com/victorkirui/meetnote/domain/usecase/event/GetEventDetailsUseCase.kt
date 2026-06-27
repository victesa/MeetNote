package com.victorkirui.meetnote.domain.usecase.event

import com.victorkirui.meetnote.domain.model.EventDetailsModel
import com.victorkirui.meetnote.domain.repository.EventsRepository
import kotlinx.coroutines.flow.Flow

class GetEventDetailsUseCase(
    private val repository: EventsRepository
) {
    operator fun invoke(id: Long): Flow<Result<EventDetailsModel>> {
        return repository.getEventDetails(id)
    }
}
