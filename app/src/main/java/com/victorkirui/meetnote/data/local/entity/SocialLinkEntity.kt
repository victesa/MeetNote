package com.victorkirui.meetnote.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "social_links",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ContactEntity::class,
            parentColumns = ["id"],
            childColumns = ["contactId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("profileId"), Index("contactId")]
)
data class SocialLinkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val profileId: Long? = null,
    val contactId: Long? = null,
    
    val platform: String,
    val url: String
)
