package com.victorkirui.meetnote.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MyProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(myProfile: MyProfile)

    @Query("SELECT * FROM MyProfile LIMIT 1")
    fun getProfileInformation(): Flow<MyProfile>

    @Update
    suspend fun update(myProfile: MyProfile)
}