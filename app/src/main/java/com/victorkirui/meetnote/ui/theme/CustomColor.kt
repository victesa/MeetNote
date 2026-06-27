package com.victorkirui.meetnote.ui.theme

import androidx.compose.ui.graphics.Color

// 1. The blueprint lives here now
data class ContactThemeColors(
    val headerColor: Color,
    val sectionColor: Color,
    val accentColor: Color,
    val subTextColor: Color = Color(0xFF64748B)
)

// 2. The concrete implementations use the blueprint locally
val workColorScheme = ContactThemeColors(
    headerColor = Color(0xFF1E1B4B),
    sectionColor = Color(0xFFF3EDF7),
    accentColor = Color(0xFF1E1B4B)
)

val socialColorScheme = ContactThemeColors(
    headerColor = Color(0xFFB34E3C),
    sectionColor = Color(0xFFFDF2F0),
    accentColor = Color(0xFFB34E3C)
)