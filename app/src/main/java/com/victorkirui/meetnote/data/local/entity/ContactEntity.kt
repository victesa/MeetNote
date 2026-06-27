package com.victorkirui.meetnote.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "contacts",
    foreignKeys = [
        ForeignKey(
            entity = EventEntity::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("eventId")]
)
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,
    
    @ColumnInfo(name = "profile_picture_uri")
    val profilePictureUri: String? = null,

    val email: String? = null,
    val phone: String? = null,
    val organization: String? = null,
    val role: String? = null,

    val eventId: Long? = null,
    val metOn: Long,
    val metAt: String? = null,
    val location: String? = null,
    val tag: String,
    val notes: String? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
