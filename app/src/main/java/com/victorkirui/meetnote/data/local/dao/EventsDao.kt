package com.victorkirui.meetnote.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.victorkirui.meetnote.data.local.entity.EventEntity
import com.victorkirui.meetnote.data.local.entity.EventWithSessions
import kotlinx.coroutines.flow.Flow

@Dao
interface EventsDao {

    @Transaction
    @Query("SELECT * FROM event")
    fun getEventsSummary(): Flow<List<EventWithSessions>>

    @androidx.room.Insert
    suspend fun insertEvent(event: EventEntity): Long

    @androidx.room.Update
    suspend fun updateEvent(event: EventEntity)

    @Query("SELECT * FROM event WHERE id = :id")
    fun getEventById(id: Long): Flow<EventEntity?>

    @Query("SELECT * FROM contacts WHERE eventId = :eventId")
    fun getContactsForEvent(eventId: Long): Flow<List<com.victorkirui.meetnote.data.local.entity.ContactEntity>>

    @Query("DELETE FROM event WHERE id = :id")
    suspend fun deleteEventById(id: Long)

    @Query("DELETE FROM contacts WHERE eventId = :eventId")
    suspend fun deleteContactsByEventId(eventId: Long)

    @Transaction
    suspend fun deleteEventWithContacts(eventId: Long) {
        deleteContactsByEventId(eventId)
        deleteEventById(eventId)
    }
}
