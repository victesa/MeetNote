package com.victorkirui.meetnote.domain.util.qr

import com.victorkirui.meetnote.presentation.state.ProfileType

data class ParsedContact(
    val fullName: String = "",
    val email: String? = null,
    val phone: String? = null,
    val organization: String? = null,
    val role: String? = null,
    val profileType: ProfileType = ProfileType.WORK,
    val socialLinks: List<Pair<String, String>> = emptyList(),
    val isValidContact: Boolean = true
)
