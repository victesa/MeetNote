package com.victorkirui.meetnote.data.mapper

import com.victorkirui.meetnote.data.local.entity.ProfileWithSocialLinks
import com.victorkirui.meetnote.domain.model.ProfileDataModel

fun ProfileWithSocialLinks.toDomain(): ProfileDataModel = ProfileDataModel(
    fullName = this.profile.name,
    email = this.profile.email,
    phoneNumber = this.profile.phone,
    organization = this.profile.organization,
    role = this.profile.role,
    profilePicture = this.profile.profilePictureUri,
    socialLinks = this.socialLinks.map { it.toDomain() },
    profileType = this.profile.profileType
)
