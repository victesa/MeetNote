package com.victorkirui.meetnote.data.mapper


import android.os.Build
import androidx.annotation.RequiresApi
import com.victorkirui.meetnote.data.dto.ContactListSummaryDTO
import com.victorkirui.meetnote.data.dto.ContactSessionSummaryDto
import com.victorkirui.meetnote.data.local.entity.ContactWithDetails
import com.victorkirui.meetnote.domain.model.ContactListModel
import com.victorkirui.meetnote.domain.model.ContactWithDetailsModel
import com.victorkirui.meetnote.domain.model.ContactSummary
import com.victorkirui.meetnote.domain.util.DateUtils
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
fun ContactSessionSummaryDto.toDomainSummary(): ContactSummary = ContactSummary(
    fullName = this.name,
    metAt = this.metAt,
    timeAgo = DateUtils.getRelativeTimeDistance(this.metOn),
    profilePicture = this.profilePictureUri,
    id = this.id,
    tag = this.tag
)

fun ContactWithDetails.toDomain(): ContactWithDetailsModel = ContactWithDetailsModel(
    id = this.contact.id,
    fullName = this.contact.name,
    firstName = this.contact.name.split(" ").firstOrNull() ?: "",
    lastName = this.contact.name.split(" ").drop(1).joinToString(" "),
    emailAddress = this.contact.email,
    phoneNumber = this.contact.phone,
    organization = this.contact.organization,
    role = this.contact.role,
    profilePictureUri = this.contact.profilePictureUri,
    location = this.contact.location,
    metAt = this.contact.metAt,
    metOn = this.contact.metOn,
    tag = this.contact.tag,
    notes = this.contact.notes,
    socialLinks = this.socialLinks.map { it.toDomain() }
)

fun ContactListSummaryDTO.toDomain(): ContactListModel = ContactListModel(
    id = this.id,
    fullName = this.fullName,
    organization = this.organization,
    role = this.role,
    metAt = this.metAt,
    profilePictureUri = this.profilePictureUri,
    tag = this.tag
)


