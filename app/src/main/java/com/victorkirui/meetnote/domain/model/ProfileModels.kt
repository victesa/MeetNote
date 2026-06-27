package com.victorkirui.meetnote.domain.model

import com.victorkirui.meetnote.domain.util.GenerateUserName

data class ProfileDataModel(
    val fullName: String = "",
    val email: String? = "",
    val phoneNumber: String? = "",
    val organization: String? = "",
    val role: String? = "",
    val socialLinks: List<SocialLinkModel> = emptyList(),
    val profilePicture: String? = "",
    val QRCode: ByteArray? = null,
    val profileType: String = "Work",
    val userName: String = GenerateUserName.generateUserNameForHomeProfileDisplayUiModel(fullName)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProfileDataModel

        if (fullName != other.fullName) return false
        if (email != other.email) return false
        if (phoneNumber != other.phoneNumber) return false
        if (organization != other.organization) return false
        if (role != other.role) return false
        if (socialLinks != other.socialLinks) return false
        if (profilePicture != other.profilePicture) return false
        if (profileType != other.profileType) return false
        if (QRCode != null) {
            if (other.QRCode == null) return false
            if (!QRCode.contentEquals(other.QRCode)) return false
        } else if (other.QRCode != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fullName.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + organization.hashCode()
        result = 31 * result + role.hashCode()
        result = 31 * result + socialLinks.hashCode()
        result = 31 * result + profilePicture.hashCode()
        result = 31 * result + (QRCode?.contentHashCode() ?: 0)
        result = 31 * result + profileType.hashCode()
        return result
    }
}

data class WorkQRCodeModel(
    val fullName: String,
    val email: String?,
    val phoneNumber: String?,
    val organization: String?,
    val role: String?,
    val socialLinkModel: List<SocialLinkModel>
)

data class SocialQRCodeModel(
    val fullName: String,
    val email: String?,
    val phoneNumber: String?,
    val socialLinks: List<SocialLinkModel>,
)
