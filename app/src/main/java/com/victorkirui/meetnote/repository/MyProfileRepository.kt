package com.victorkirui.meetnote.repository

import com.victorkirui.meetnote.data.MyProfile
import com.victorkirui.meetnote.data.MyProfileDao
import kotlinx.coroutines.flow.Flow

class MyProfileRepository(private val myProfileDao: MyProfileDao) {

    fun getProfileDetails(): Flow<MyProfile?>{
        return myProfileDao.getProfileInformation()
    }

    suspend fun saveProfile(profile: MyProfile){
        myProfileDao.save(profile)
    }
}