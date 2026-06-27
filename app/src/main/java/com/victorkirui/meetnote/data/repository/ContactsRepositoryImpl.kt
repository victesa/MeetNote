package com.victorkirui.meetnote.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.victorkirui.meetnote.data.dto.toDomainModel
import com.victorkirui.meetnote.data.local.dao.ContactsDao
import com.victorkirui.meetnote.data.local.dao.MySocialLinksDao
import com.victorkirui.meetnote.data.local.entity.ContactEntity
import com.victorkirui.meetnote.data.local.entity.SocialLinkEntity
import com.victorkirui.meetnote.data.mapper.toDomain
import com.victorkirui.meetnote.data.mapper.toDomainSummary
import com.victorkirui.meetnote.domain.model.AddContactModel
import com.victorkirui.meetnote.domain.model.ContactListModel
import com.victorkirui.meetnote.domain.model.ContactWithDetailsModel
import com.victorkirui.meetnote.domain.model.ContactSummary
import com.victorkirui.meetnote.domain.model.ContactSummaryForAddContactScreenModel
import com.victorkirui.meetnote.domain.repository.ContactsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class ContactsRepositoryImpl(
    private val contactsDao: ContactsDao,
    private val socialLinksDao: MySocialLinksDao
): ContactsRepository {
@RequiresApi(Build.VERSION_CODES.O)
    override fun getContactSessionSummaryList(): Flow<Result<List<ContactSummary>>> {
        return contactsDao.getAllContactSessionSummary().map {list->
            val summary = list.map {contactsDto->
                ContactSummary(
                    fullName = contactsDto.name,
                    metAt = contactsDto.metAt,
                    timeAgo = com.victorkirui.meetnote.domain.util.DateUtils.getRelativeTimeDistance(contactsDto.metOn),
                    profilePicture = contactsDto.profilePictureUri,
                    id = contactsDto.id,
                    tag = contactsDto.tag
                )
            }

            Result.success(summary)
        }.catch {
            emit(Result.failure(it))
        }
    }

    override fun getContactWithDetails(id: Long): Flow<Result<ContactWithDetailsModel>> {
        val result =  contactsDao.getContactWithDetailsById(id).map { contactWithDetails ->
            if (contactWithDetails == null){
                Result.failure(ContactNotFoundException(id))
            }else{
                Result.success(contactWithDetails.toDomain())
            }
        }
            .catch {
                emit(Result.failure(exception = it))
            }

        return result
    }

    override fun getAllContactListSummary(searchQuery: String, tag: String?): Flow<Result<List<ContactListModel>>> {
        val formattedQuery = "%$searchQuery%"
        return contactsDao.getAllContactListSummary(formattedQuery, tag)
            .map { dtoList ->
                runCatching {
                    dtoList.map { dto -> dto.toDomain() }
                }
            }
            .catch { exception ->
                emit(Result.failure(exception))
            }
    }

    override suspend fun saveContact(addContactModel: AddContactModel): Result<Long> {
        return runCatching {
            val contactId = contactsDao.insertContact(
                ContactEntity(
                    name = addContactModel.fullName,
                    email = addContactModel.email,
                    phone = addContactModel.phoneNumber,
                    tag = addContactModel.tag,
                    profilePictureUri = addContactModel.profilePictureUri,
                    metAt = addContactModel.metAt,
                    eventId = addContactModel.eventId,
                    metOn = addContactModel.metOn,
                    organization = addContactModel.organization,
                    role = addContactModel.role,
                    location = addContactModel.location,
                    notes = addContactModel.notes
                )
            )
            
            if (addContactModel.socialLinks.isNotEmpty()) {
                socialLinksDao.insertLinks(
                    addContactModel.socialLinks.map { 
                        SocialLinkEntity(
                            contactId = contactId,
                            platform = it.platform,
                            url = it.url
                        )
                    }
                )
            }
            
            contactId
        }
    }

    override fun getContactSummaryForAddContactScenarios(id: Long): Flow<Result<ContactSummaryForAddContactScreenModel>> {
        return contactsDao.getContactSummaryForAddContactScreen(id).map { dto ->
            runCatching {
                if (dto == null) throw NoSuchElementException("Contact ID $id not found")
                dto.toDomainModel()
            }
        }
    }

    override suspend fun updateMoreDetails(model: ContactWithDetailsModel): Result<Unit> {
        return runCatching {
            contactsDao.updateContactMoreDetailsFields(
                id = model.id,
                fullName = model.fullName,
                email = model.emailAddress,
                phone = model.phoneNumber,
                tag = model.tag,
                organization = model.organization,
                role = model.role,
                location = model.location,
                notes = model.notes
            )
            
            socialLinksDao.deleteLinksForContact(model.id)
            socialLinksDao.insertLinks(
                model.socialLinks.map { 
                    SocialLinkEntity(
                        contactId = model.id,
                        platform = it.platform,
                        url = it.url
                    )
                }
            )
        }
    }

    override suspend fun deleteContact(id: Long): Result<Unit> {
        return runCatching {
            contactsDao.deleteContactById(id)
        }
    }
}

class ContactNotFoundException(id: Long) : Exception("Contact with ID $id could not be found in the database.")
