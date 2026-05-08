package com.victorkirui.meetnote.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MetPerson(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 1,

    val name: String,

    val phone: String?,
    val email: String?,

    val note: String?,
    val metAt: String?,
    val metOn: Long,

    val createdAt: Long = System.currentTimeMillis()
)
