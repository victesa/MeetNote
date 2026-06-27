package com.victorkirui.meetnote.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.victorkirui.meetnote.data.local.entity.SocialLinkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MySocialLinksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLinks(socialLinkEntities: List<SocialLinkEntity>)

    @Query("DELETE FROM social_links WHERE contactId = :contactId")
    suspend fun deleteLinksForContact(contactId: Long)

    @Query("SELECT * FROM social_links")
    fun getAllSocialLinks(): Flow<List<SocialLinkEntity>>
}
