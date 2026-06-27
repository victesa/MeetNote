package com.victorkirui.meetnote.domain.usecase.contact

import com.victorkirui.meetnote.domain.model.ContactWithDetailsModel
import com.victorkirui.meetnote.domain.repository.ContactsRepository

class UpdateContactMoreDetailsUseCase(
    private val repository: ContactsRepository
) {
    suspend operator fun invoke(model: ContactWithDetailsModel): Result<Unit> {
        return repository.updateMoreDetails(model)
    }
}
