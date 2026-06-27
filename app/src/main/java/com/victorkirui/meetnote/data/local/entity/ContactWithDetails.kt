package com.victorkirui.meetnote.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ContactWithDetails(
    @Embedded 
    val contact: ContactEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "contactId"
    )
    val socialLinks: List<SocialLinkEntity>
)
