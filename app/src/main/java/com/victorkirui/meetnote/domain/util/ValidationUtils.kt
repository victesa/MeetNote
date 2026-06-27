package com.victorkirui.meetnote.domain.util

import com.victorkirui.meetnote.domain.model.SocialLinkModel

object ValidationUtils {
    // Standard email validation regex
    private val EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()

    // Example phone regex: allows digits, optional spaces/dashes, and a length of 10-15 digits
    private val PHONE_REGEX = "^\\+?[0-9\\s\\-]{10,15}$".toRegex()

    fun isValidEmail(email: String?): Boolean {
        return email?.let { EMAIL_REGEX.matches(it) } == true
    }

    fun isValidPhoneNumber(phone: String?): Boolean {
        // 1. Strip out spaces and dashes
        val cleanPhone = phone?.replace(Regex("[\\s\\-]"), "")

        // 2. Check the cleaned string directly against the regex
        return cleanPhone != null && PHONE_REGEX.matches(cleanPhone)
    }

    fun isSocialProfileValid(
        socialLinks: List<SocialLinkModel>,
        phoneNumber: String?,
        email: String?
    ): Boolean{
        val hasSocialLinks = socialLinks.isNotEmpty()
        val hasPhoneNumber = phoneNumber?.isNotEmpty()
        val hasEmail = email?.isNotEmpty()

        return hasSocialLinks || hasPhoneNumber == true || hasEmail == true
    }
}
