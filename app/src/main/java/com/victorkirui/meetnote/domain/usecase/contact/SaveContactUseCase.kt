package com.victorkirui.meetnote.domain.usecase.contact

import com.victorkirui.meetnote.domain.model.AddContactModel
import com.victorkirui.meetnote.domain.repository.ContactsRepository

class SaveContactUseCase(
    private val repository: ContactsRepository
) {
    suspend operator fun invoke(contact: AddContactModel): Result<Long> {
        return repository.saveContact(contact)
    }
}
