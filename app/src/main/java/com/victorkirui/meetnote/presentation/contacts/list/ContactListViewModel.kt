package com.victorkirui.meetnote.presentation.contacts.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victorkirui.meetnote.domain.repository.ContactsRepository
import com.victorkirui.meetnote.presentation.state.ProfileType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContactListViewModel(
    private val contactRepository: ContactsRepository
): ViewModel() {

    private val _uiQuery = MutableStateFlow("")

    private val _selectedTag = MutableStateFlow<ProfileType?>(null)
    val selectedTag: StateFlow<ProfileType?> = _selectedTag.asStateFlow()

    private val _selectedIds = MutableStateFlow<Set<Long>>(emptySet())
    private val _isSelectionMode = MutableStateFlow(false)

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ContactListState> = combine(
        _uiQuery,
        selectedTag,
        _selectedIds,
        _isSelectionMode
    ) { query, tag, selectedIds, isSelectionMode ->
        Triple(query, tag, selectedIds to isSelectionMode)
    }
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { (query, tag, selection) ->
            val (selectedIds, isSelectionMode) = selection
            contactRepository.getAllContactListSummary(query, tag?.name).map { result ->
                result.fold(
                    onSuccess = {
                        ContactListState.Success(
                            contactListUiState = it.map { contactListModel -> contactListModel.toUiState() },
                            isSearching = false,
                            searchQuery = query,
                            selectedIds = selectedIds,
                            isSelectionMode = isSelectionMode
                        )
                    },
                    onFailure = {
                        ContactListState.Error(
                            errorMessage = it.message ?: "An unknown error has occurred"
                        )
                    }
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ContactListState.Loading
        )

    fun onSearchQueryChange(query: String) {
        _uiQuery.value = query
    }

    fun onTagSelected(tag: ProfileType?) {
        _selectedTag.value = tag
    }

    fun toggleSelection(contactId: Long) {
        _selectedIds.update { current ->
            if (current.contains(contactId)) {
                val next = current - contactId
                if (next.isEmpty()) _isSelectionMode.value = false
                next
            } else {
                _isSelectionMode.value = true
                current + contactId
            }
        }
    }

    fun enterSelectionMode(contactId: Long) {
        _isSelectionMode.value = true
        _selectedIds.value = setOf(contactId)
    }

    fun exitSelectionMode() {
        _isSelectionMode.value = false
        _selectedIds.value = emptySet()
    }

    fun deleteSelectedContacts() {
        viewModelScope.launch {
            val idsToDelete = _selectedIds.value
            idsToDelete.forEach { id ->
                contactRepository.deleteContact(id)
            }
            exitSelectionMode()
        }
    }
}
