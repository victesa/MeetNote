package com.victorkirui.meetnote.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class ProfileEntity(
    @PrimaryKey val id: Long,
    val profileType: String,
    val profilePictureUri: String?,
    val name: String,
    val email: String?,
    val phone: String?,
    val organization: String?,
    val role: String?
)
