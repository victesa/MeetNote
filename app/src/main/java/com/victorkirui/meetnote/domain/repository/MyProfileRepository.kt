package com.victorkirui.meetnote.domain.repository

import com.victorkirui.meetnote.data.local.entity.SocialLinkEntity
import com.victorkirui.meetnote.domain.model.ProfileDataModel
import kotlinx.coroutines.flow.Flow

interface MyProfileRepository {

    suspend fun saveProfile(profile: ProfileDataModel): Result<Unit>

    fun getSocialLinks(): Flow<List<SocialLinkEntity>>

    suspend fun saveSocialLinks(socialLinkEntities: List<SocialLinkEntity>)

    suspend fun saveImageToInternalStorage(temporaryUri: String): Result<String>

    fun getSocialProfile(): Flow<Result<ProfileDataModel>>

    fun getWorkProfile(): Flow<Result<ProfileDataModel>>
}
