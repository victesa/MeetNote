package com.victorkirui.meetnote

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.victorkirui.meetnote.data.local.AppDatabase
import com.victorkirui.meetnote.data.local.dao.ContactsDao
import com.victorkirui.meetnote.data.local.entity.ContactEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContactsDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: ContactsDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.contactsDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetAllContacts() = runBlocking {
        val contact = ContactEntity(
            id = 1,
            name = "John Doe",
            tag = "WORK",
            metOn = System.currentTimeMillis()
        )
        dao.insertContact(contact)

        // Test with exact match (what the code currently does)
        val resultExact = dao.getAllContactListSummary("John Doe", null).first()
        assertEquals(1, resultExact.size)
        assertEquals("John Doe", resultExact[0].fullName)

        // Test with empty string (what the search bar starts with)
        val resultEmpty = dao.getAllContactListSummary("", null).first()
        // If this is 0, it explains why contacts don't show up by default
        assertEquals("Empty search should return contacts if query is formatted correctly", 1, resultEmpty.size)
    }

    @Test
    fun testSearchQueryFormatting() = runBlocking {
        val contact = ContactEntity(
            id = 1,
            name = "John Doe",
            tag = "WORK",
            metOn = System.currentTimeMillis()
        )
        dao.insertContact(contact)

        // The current implementation passes the query directly.
        // Let's see if "%" works as expected.
        val resultWildcard = dao.getAllContactListSummary("%", null).first()
        assertEquals(1, resultWildcard.size)
    }
}
