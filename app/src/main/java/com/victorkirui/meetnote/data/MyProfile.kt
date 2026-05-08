package com.victorkirui.meetnote.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MyProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,
    val phone: String?,
    val email: String?,
    val organization: String?,
    val role: String?,
    val profileType: String
)
