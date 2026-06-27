package com.victorkirui.meetnote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victorkirui.meetnote.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val prefsRepo: UserPreferencesRepository): ViewModel() {

    val appState: StateFlow<AppState> = prefsRepo.isFirstLaunch
        .map { isFirstLaunch ->
            if (isFirstLaunch) {
                AppState.FirstTimeUser
            } else {
                AppState.FullyOnboarded
            }
        }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppState.Loading
    )

    fun completeOnboarding() {
        viewModelScope.launch {
            prefsRepo.markFirstLaunchComplete()
        }
    }

    fun completeProfileSetup(){
        viewModelScope.launch {
            prefsRepo.markProfileSetupComplete()
        }
    }
}

sealed interface AppState {
    data object Loading : AppState
    data object FirstTimeUser : AppState
    data object FullyOnboarded : AppState
}