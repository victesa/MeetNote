package com.victorkirui.meetnote.presentation.contacts.list

import com.victorkirui.meetnote.domain.model.ContactListModel

data class ContactListUiState(
    val id: Long = 0,
    val fullName: String = "",
    val profilePictureUri: String? = null,
    val role: String? = null,
    val organization: String? = null,
    val metAt: String? = null,
    val tag: String? = null
)

fun ContactListModel.toUiState(): ContactListUiState = ContactListUiState(
    id = this.id,
    fullName = this.fullName,
    profilePictureUri = this.profilePictureUri,
    role = this.role,
    organization = this.organization,
    metAt = this.metAt,
    tag = this.tag
)

sealed interface ContactListState {
    data object Loading : ContactListState

    data class Success(
        val contactListUiState: List<ContactListUiState>,
        val searchQuery: String,
        val isSearching: Boolean = false,
        val selectedIds: Set<Long> = emptySet(),
        val isSelectionMode: Boolean = false
    ) : ContactListState

    data class Error(val errorMessage: String) : ContactListState
}
