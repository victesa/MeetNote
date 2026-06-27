package com.victorkirui.meetnote.domain.util.qr

import com.victorkirui.meetnote.presentation.state.ProfileType

object QRCodeParser {
    fun parse(content: String): ParsedContact {
        val trimmed = content.trim()
        android.util.Log.d("DEBUG_SCAN", "Step 6.1: Parser received: $trimmed")
        return when {
            trimmed.startsWith("BEGIN:VCARD", ignoreCase = true) -> {
                android.util.Log.d("DEBUG_SCAN", "Step 6.2: Identified as vCard")
                parseVCard(trimmed)
            }
            trimmed.startsWith("MeetNote Profile", ignoreCase = true) -> {
                android.util.Log.d("DEBUG_SCAN", "Step 6.2: Identified as MeetNote Social Profile")
                parseSocialProfile(trimmed)
            }
            else -> {
                android.util.Log.d("DEBUG_SCAN", "Step 6.2: Unrecognized format")
                ParsedContact(isValidContact = false)
            }
        }
    }

    private fun parseVCard(content: String): ParsedContact {
        var fullName = ""
        var email: String? = null
        var phone: String? = null
        var org: String? = null
        var role: String? = null
        val socialLinks = mutableListOf<Pair<String, String>>()

        val lines = content.lines()
        for (line in lines) {
            val upperLine = line.uppercase().trim()
            when {
                upperLine.startsWith("FN:") -> {
                    if (fullName.isBlank()) fullName = line.substring(line.indexOf(":") + 1).trim()
                }
                upperLine.startsWith("N:") -> {
                    if (fullName.isBlank()) {
                        val nameParts = line.substring(line.indexOf(":") + 1).split(";")
                        val last = nameParts.getOrNull(0)?.trim() ?: ""
                        val first = nameParts.getOrNull(1)?.trim() ?: ""
                        fullName = "$first $last".trim()
                    }
                }
                upperLine.startsWith("TEL") && upperLine.contains(":") -> phone = line.substring(line.indexOf(":") + 1).trim()
                upperLine.startsWith("EMAIL") && upperLine.contains(":") -> email = line.substring(line.indexOf(":") + 1).trim()
                upperLine.startsWith("ORG:") -> org = line.substring(line.indexOf(":") + 1).trim()
                upperLine.startsWith("TITLE:") -> role = line.substring(line.indexOf(":") + 1).trim()
                upperLine.startsWith("URL:") -> socialLinks.add("Link" to line.substring(line.indexOf(":") + 1).trim())
            }
        }

        return ParsedContact(
            fullName = fullName,
            email = email,
            phone = phone,
            organization = org,
            role = role,
            profileType = ProfileType.WORK
        )
    }

    private fun parseSocialProfile(content: String): ParsedContact {
        var fullName = ""
        var email: String? = null
        var phone: String? = null
        val socialLinks = mutableListOf<Pair<String, String>>()

        val lines = content.lines()
        for (line in lines) {
            val trimmedLine = line.trim()
            when {
                trimmedLine.startsWith("Name: ") -> fullName = trimmedLine.substring(6).trim()
                trimmedLine.startsWith("Phone: ") -> phone = trimmedLine.substring(7).trim()
                trimmedLine.startsWith("Email: ") -> email = trimmedLine.substring(7).trim()
                trimmedLine.contains(": ") -> {
                    val parts = trimmedLine.split(": ", limit = 2)
                    if (parts.size == 2) {
                        val key = parts[0].trim()
                        val value = parts[1].trim()
                        if (key != "Name" && key != "Phone" && key != "Email" && key != "MeetNote Profile") {
                            socialLinks.add(key to value)
                        }
                    }
                }
            }
        }

        return ParsedContact(
            fullName = fullName,
            email = email,
            phone = phone,
            profileType = ProfileType.SOCIAL,
            socialLinks = socialLinks
        )
    }
}
