package com.victorkirui.meetnote.domain.util

object GenerateUserName {
    fun generateUserNameForHomeProfileDisplayUiModel(fullName: String): String {
        val words = fullName.trim().split("\\s+".toRegex())
        if (words.isEmpty() || words[0].isEmpty()) return ""
        val firstWord = words[0]
        val secondInitial = words.getOrNull(1)?.take(1) ?: ""
        return "@$firstWord$secondInitial"
    }
}