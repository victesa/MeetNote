package com.victorkirui.meetnote.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victorkirui.meetnote.domain.repository.MyProfileRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AccountViewModel(
    private val profileRepository: MyProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchProfiles()
    }

    private fun fetchProfiles() {
        viewModelScope.launch {
            combine(
                profileRepository.getWorkProfile(),
                profileRepository.getSocialProfile()
            ) { workResult, socialResult ->
                _uiState.update { it.copy(
                    workProfile = workResult.getOrNull(),
                    socialProfile = socialResult.getOrNull(),
                    isLoading = false
                ) }
            }.collect()
        }
    }
}
