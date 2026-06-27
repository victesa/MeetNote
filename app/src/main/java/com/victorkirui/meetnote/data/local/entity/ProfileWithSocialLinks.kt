package com.victorkirui.meetnote.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ProfileWithSocialLinks (
    @Embedded
    val profile: ProfileEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "profileId"
    )
    val socialLinks: List<SocialLinkEntity>
)
