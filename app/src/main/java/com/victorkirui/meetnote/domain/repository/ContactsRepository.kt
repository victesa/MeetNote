package com.victorkirui.meetnote.domain.repository

import com.victorkirui.meetnote.data.local.entity.ContactEntity
import com.victorkirui.meetnote.domain.model.AddContactModel
import com.victorkirui.meetnote.domain.model.ContactListModel
import com.victorkirui.meetnote.domain.model.ContactWithDetailsModel
import com.victorkirui.meetnote.domain.model.ContactSummary
import com.victorkirui.meetnote.domain.model.ContactSummaryForAddContactScreenModel
import kotlinx.coroutines.flow.Flow

interface ContactsRepository {

    fun getContactSessionSummaryList(): Flow<Result<List<ContactSummary>>>

    fun getContactWithDetails(id: Long): Flow<Result<ContactWithDetailsModel>>

    fun getAllContactListSummary(searchQuery: String, tag: String?): Flow<Result<List<ContactListModel>>>

    suspend fun saveContact(addContactModel: AddContactModel): Result<Long>

    fun getContactSummaryForAddContactScenarios(id: Long): Flow<Result<ContactSummaryForAddContactScreenModel>>

    suspend fun updateMoreDetails(model: ContactWithDetailsModel): Result<Unit>

    suspend fun deleteContact(id: Long): Result<Unit>
}
