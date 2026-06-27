package com.victorkirui.meetnote.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.victorkirui.meetnote.data.dto.ContactListSummaryDTO
import com.victorkirui.meetnote.data.dto.ContactSessionSummaryDto
import com.victorkirui.meetnote.data.dto.ContactSummaryForAddContactScreenDto
import com.victorkirui.meetnote.data.local.entity.ContactEntity
import com.victorkirui.meetnote.data.local.entity.ContactWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity): Long

    @Transaction
    @Query("SELECT * FROM contacts")
    fun getAllContactsWithDetails(): Flow<List<ContactWithDetails>>

    @Transaction
    @Query("SELECT * FROM contacts WHERE id = :id")
    fun getContactWithDetailsById(id: Long): Flow<ContactWithDetails?>

    @Query("""
        SELECT 
            id, 
            name, 
            profile_picture_uri AS profilePictureUri, 
            metOn, 
            metAt,
            tag
        FROM contacts
        ORDER BY metOn DESC
    """)
    fun getAllContactSessionSummary(): Flow<List<ContactSessionSummaryDto>>


    @Query("""SELECT  
        id,
        name as fullName,
        organization,
        role,
        profile_picture_uri as profilePictureUri,
        metAt,
        tag
        FROM contacts where (name LIKE :searchQuery OR organization LIKE :searchQuery) AND (:tag IS NULL OR tag = :tag) ORDER BY name ASC""")
    fun getAllContactListSummary(searchQuery: String, tag: String?): Flow<List<ContactListSummaryDTO>>

    @Query("DELETE FROM contacts WHERE id = :id")
    suspend fun deleteContactById(id: Long)

    @Query("""SELECT 
        id,
        name as fullName,
        profile_picture_uri as profilePictureUri,
        email as emailAddress,
        phone as phoneNumber,
        tag
        FROM contacts WHERE id = :id
    """)
    fun getContactSummaryForAddContactScreen(id: Long): Flow<ContactSummaryForAddContactScreenDto?>

    @Query("""
    UPDATE contacts 
    SET name = :fullName,
        email = :email,
        phone = :phone,
        tag = :tag,
        organization = :organization, 
        role = :role, 
        location = :location, 
        notes = :notes 
    WHERE id = :id
""")
    suspend fun updateContactMoreDetailsFields(
        id: Long,
        fullName: String,
        email: String?,
        phone: String?,
        tag: String,
        organization: String?,
        role: String?,
        location: String?,
        notes: String?
    )
}
