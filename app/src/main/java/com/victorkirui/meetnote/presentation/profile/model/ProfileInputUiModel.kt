package com.victorkirui.meetnote.presentation.profile.model

import com.victorkirui.meetnote.presentation.state.ProfileType
import com.victorkirui.meetnote.presentation.state.SocialLinkState

data class ProfileInputUiModel(
    val fullName: String,
    val email: String?,
    val phoneNumber: String?,
    val organization: String?,
    val role: String,
    val socialLinkState: List<SocialLinkState>,
    val profilePictureUri: String?,
    val profileType: ProfileType
)
