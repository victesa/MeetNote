package com.victorkirui.meetnote.presentation.navigation

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Onboarding2 : Screen("onboarding2")
    data object Home : Screen("home")
    data object ContactList : Screen("contact_list")
    data object Events : Screen("events")
    data object AddContact : Screen("add_contact")
    data object EventDetails : Screen("event_details/{event_id}") {
        fun createRoute(eventId: Long): String {
            return "event_details/$eventId"
        }
    }
    data object ContactMoreDetails : Screen("contact_more_details/{contact_id}?is_edit={is_edit}") {
        fun createRoute(contactId: Long, isEdit: Boolean = false): String {
            return "contact_more_details/$contactId?is_edit=$isEdit"
        }
    }
    data object ProfileSetup: Screen("profile_setup")
    data object SocialProfileSetup: Screen("social_profile_setup")
    data object AddEvent : Screen("add_event?event_id={event_id}") {
        fun createRoute(eventId: Long? = null): String {
            return if (eventId != null) "add_event?event_id=$eventId" else "add_event"
        }
    }
    data object Account : Screen("account")
    data object Scan : Screen("scan")
    data object ScannedContact : Screen("scanned_contact")
    data object ScanError : Screen("scan_error")
    data object QRCodeShare : Screen("qr_code_share/{profile_type}") {
        fun createRoute(profileType: String): String {
            return "qr_code_share/$profileType"
        }
    }
    
    data object ContactDetails : Screen("contact_details/{contact_id}/{contact_tag}") {
        fun createRoute(contactId: Long, contactTag: String): String {
            return "contact_details/$contactId/$contactTag"
        }
    }
}
