package com.victorkirui.meetnote.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.victorkirui.meetnote.data.local.entity.ProfileEntity
import com.victorkirui.meetnote.data.local.entity.SocialLinkEntity
import com.victorkirui.meetnote.data.local.entity.ProfileWithSocialLinks
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(profileEntity: ProfileEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSocialLinks(links: List<SocialLinkEntity>)

    @Update
    suspend fun update(profileEntity: ProfileEntity)

    @Transaction
    suspend fun insertProfileWithLinks(profile: ProfileEntity, links: List<SocialLinkEntity>){
        val generatedProfileId = save(profile)

        val updatedLinks = links.map {link->
            link.copy(profileId = generatedProfileId)
        }

        insertSocialLinks(updatedLinks)
    }

    @Transaction
    @Query("SELECT * FROM user_profile WHERE profileType = 'Social' LIMIT 1")
    fun getSocialProfileWithSocialLinks(): Flow<ProfileWithSocialLinks?>

    @Transaction
    @Query("SELECT * FROM user_profile WHERE profileType = 'Work' LIMIT 1")
    fun getWorkProfileWithSocialLinks(): Flow<ProfileWithSocialLinks?>
}
