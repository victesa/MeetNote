package com.victorkirui.meetnote.data.mapper

import com.victorkirui.meetnote.data.local.entity.SocialLinkEntity
import com.victorkirui.meetnote.domain.model.SocialLinkModel

fun SocialLinkEntity.toDomain(): SocialLinkModel = SocialLinkModel(
    url = this.url,
    platform = this.platform,
    id =this.id
)
