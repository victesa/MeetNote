package com.victorkirui.meetnote.presentation.account

import com.victorkirui.meetnote.domain.model.ProfileDataModel

data class AccountUiState(
    val workProfile: ProfileDataModel? = null,
    val socialProfile: ProfileDataModel? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
