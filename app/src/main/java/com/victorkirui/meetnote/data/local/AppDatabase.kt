package com.victorkirui.meetnote.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.victorkirui.meetnote.data.local.dao.ContactsDao
import com.victorkirui.meetnote.data.local.dao.EventsDao
import com.victorkirui.meetnote.data.local.dao.ProfileDao
import com.victorkirui.meetnote.data.local.dao.MySocialLinksDao
import com.victorkirui.meetnote.data.local.entity.ProfileEntity
import com.victorkirui.meetnote.data.local.entity.SocialLinkEntity
import com.victorkirui.meetnote.data.local.entity.EventEntity
import com.victorkirui.meetnote.data.local.entity.ContactEntity

@Database(
    entities = [
        ProfileEntity::class,
        SocialLinkEntity::class,
        EventEntity::class,
        ContactEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    companion object{
        const val DATABASE_NAME = "AppDatabase"
    }

    abstract fun myProfile(): ProfileDao
    abstract fun mySocialLinksDao(): MySocialLinksDao
    abstract fun contactsDao(): ContactsDao
    abstract fun eventsDao(): EventsDao
}
