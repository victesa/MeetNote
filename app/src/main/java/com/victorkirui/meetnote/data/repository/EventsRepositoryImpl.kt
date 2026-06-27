package com.victorkirui.meetnote.data.repository

import com.victorkirui.meetnote.data.local.dao.EventsDao
import com.victorkirui.meetnote.data.mapper.toDomainEventWithSessionModel
import com.victorkirui.meetnote.domain.model.EventsSummary
import com.victorkirui.meetnote.domain.repository.EventsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class EventsRepositoryImpl(
    private val eventsDao: EventsDao
): EventsRepository {
    override fun getAllEventsWithSessions(): Flow<Result<List<EventsSummary>>> {
        return eventsDao.getEventsSummary().map {list->
            val domainEvents = list.map { it.toDomainEventWithSessionModel() }
            Result.success(domainEvents)
        }.catch {exception->
            emit(Result.failure(exception))
        }
    }

    override suspend fun saveEvent(event: com.victorkirui.meetnote.domain.model.AddEventModel): Result<Long> {
        return try {
            val entity = com.victorkirui.meetnote.data.local.entity.EventEntity(
                id = event.id,
                name = event.name,
                eventDate = event.date,
                location = event.location,
                eventType = event.eventType,
                notes = event.notes
            )
            val id = if (event.id == 0L) {
                eventsDao.insertEvent(entity)
            } else {
                eventsDao.updateEvent(entity)
                event.id
            }
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getEventDetails(id: Long): Flow<Result<com.victorkirui.meetnote.domain.model.EventDetailsModel>> {
        return kotlinx.coroutines.flow.combine(
            eventsDao.getEventById(id),
            eventsDao.getContactsForEvent(id)
        ) { event, contacts ->
            if (event == null) {
                Result.failure(Exception("Event not found"))
            } else {
                val details = com.victorkirui.meetnote.domain.model.EventDetailsModel(
                    id = event.id,
                    name = event.name,
                    date = event.eventDate,
                    location = event.location,
                    eventType = event.eventType,
                    notes = event.notes,
                    contactsExchanged = contacts.size,
                    contactsMet = contacts.map { contact ->
                        com.victorkirui.meetnote.domain.model.ContactMetSummary(
                            id = contact.id,
                            fullName = contact.name,
                            role = contact.role,
                            organization = contact.organization,
                            profilePictureUri = contact.profilePictureUri,
                            tag = contact.tag
                        )
                    }
                )
                Result.success(details)
            }
        }.catch { emit(Result.failure(it)) }
    }

    override suspend fun deleteEvent(id: Long, deleteContacts: Boolean): Result<Unit> {
        return try {
            if (deleteContacts) {
                eventsDao.deleteEventWithContacts(id)
            } else {
                eventsDao.deleteEventById(id)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}