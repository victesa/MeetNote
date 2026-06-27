package com.victorkirui.meetnote.domain.usecase.contact

import com.victorkirui.meetnote.domain.model.ContactSummaryForAddContactScreenModel
import com.victorkirui.meetnote.domain.repository.ContactsRepository
import kotlinx.coroutines.flow.Flow

class GetContactSummaryUseCase(
    private val repository: ContactsRepository
) {
    operator fun invoke(id: Long): Flow<Result<ContactSummaryForAddContactScreenModel>> {
        return repository.getContactSummaryForAddContactScenarios(id)
    }
}
