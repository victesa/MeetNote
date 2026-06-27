package com.victorkirui.meetnote.domain.usecase.event

import com.victorkirui.meetnote.domain.model.AddEventModel
import com.victorkirui.meetnote.domain.repository.EventsRepository

class SaveEventUseCase(
    private val repository: EventsRepository
) {
    suspend operator fun invoke(event: AddEventModel): Result<Long> {
        return repository.saveEvent(event)
    }
}
