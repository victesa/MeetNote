package com.victorkirui.meetnote.data.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import androidx.core.net.toUri
import com.victorkirui.meetnote.data.local.dao.ProfileDao
import com.victorkirui.meetnote.data.local.dao.MySocialLinksDao
import com.victorkirui.meetnote.data.local.entity.ProfileEntity
import com.victorkirui.meetnote.data.local.entity.SocialLinkEntity
import com.victorkirui.meetnote.data.mapper.toDomain
import com.victorkirui.meetnote.domain.model.ProfileDataModel
import com.victorkirui.meetnote.domain.model.SocialLinkModel
import com.victorkirui.meetnote.domain.repository.MyProfileRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.File
import java.util.UUID

class MyProfileRepositoryImpl(private val profileDao: ProfileDao,
                              private val mySocialLinksDao: MySocialLinksDao,
                              private val context: Context
):
    MyProfileRepository {

    override suspend fun saveProfile(profile: ProfileDataModel): Result<Unit> = runCatching {

        val profileData = ProfileEntity(
            id = if (profile.profileType == "Work") 1L else 2L,
            name = profile.fullName,
            email = profile.email,
            phone = profile.phoneNumber,
            organization = profile.organization,
            profilePictureUri = profile.profilePicture,
            profileType = profile.profileType,
            role = profile.role
        )

        val socialLinkEntities = profile.socialLinks.map { domainLink ->
            SocialLinkEntity(
                platform = domainLink.platform,
                url = domainLink.url,
                profileId = if (profile.profileType == "Work") 1L else 2L
            )
        }


        profileDao.insertProfileWithLinks(profileData, socialLinkEntities)
    }

    override fun getSocialLinks(): Flow<List<SocialLinkEntity>> {
        return mySocialLinksDao.getAllSocialLinks()
    }

    override suspend fun saveSocialLinks(socialLinkEntities: List<SocialLinkEntity>) {
        mySocialLinksDao.insertLinks(socialLinkEntities)
    }


    override suspend fun saveImageToInternalStorage(temporaryUri: String): Result<String> {
        return runCatching {
            val context = context
            val sourceUri = temporaryUri.toUri()

            val uniqueFileName = "profile_${UUID.randomUUID()}.jpg"

            val destinationFile = File(context.filesDir, uniqueFileName)

            context.contentResolver.openInputStream(sourceUri).use { inputStream ->
                if(inputStream == null) throw IllegalStateException("Could not open Image Stream")

                destinationFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            destinationFile.absoluteFile.toString()

        }
    }

    override fun getSocialProfile(): Flow<Result<ProfileDataModel>> {
        val profileData = profileDao.getSocialProfileWithSocialLinks()

        return profileData.map { profile ->
            val nonNullRelation = requireNotNull(profile){"Profile is Missing! Onboarding constraint violated."}

            val model = nonNullRelation.toDomain()

            Result.success(model)

        }.catch {exception->
            emit(Result.failure(exception))
        }
    }

    override fun getWorkProfile(): Flow<Result<ProfileDataModel>> {
        return profileDao.getWorkProfileWithSocialLinks().map { profile->
            val nonNullResult = requireNotNull(profile){"Profile is Missing! Onboarding constraint violated."}

            val model = nonNullResult.toDomain()

            Result.success(model)
        }
            .catch { exception->
                emit(Result.failure(exception))
            }

    }
}