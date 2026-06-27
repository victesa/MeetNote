package com.victorkirui.meetnote.presentation.state

import com.victorkirui.meetnote.domain.model.SocialLinkModel

data class SocialLinkState(
    val id: Long = 0,
    val platform: String = "",
    val url: String = ""
)

fun SocialLinkState.toDomain(): SocialLinkModel = SocialLinkModel(
    platform = this.platform,
    url = this.url
)
