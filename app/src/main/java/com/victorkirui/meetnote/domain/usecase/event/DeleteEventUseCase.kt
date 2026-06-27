package com.victorkirui.meetnote.domain.usecase.event

import com.victorkirui.meetnote.domain.repository.EventsRepository

class DeleteEventUseCase(
    private val repository: EventsRepository
) {
    suspend operator fun invoke(id: Long, deleteContacts: Boolean): Result<Unit> {
        return repository.deleteEvent(id, deleteContacts)
    }
}
