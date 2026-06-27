package com.victorkirui.meetnote.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victorkirui.meetnote.domain.model.ProfileDataModel
import com.victorkirui.meetnote.domain.model.SocialQRCodeModel
import com.victorkirui.meetnote.domain.model.WorkQRCodeModel
import com.victorkirui.meetnote.domain.repository.MyProfileRepository
import com.victorkirui.meetnote.domain.repository.QRCodeGenerator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class QRCodeShareScreenViewModel(
    private val profileRepository: MyProfileRepository,
    private val qrCodeGenerator: QRCodeGenerator
) : ViewModel() {

    private val _uiWorkProfileState = MutableStateFlow<ProfileDataModel?>(null)
    val uiWorkProfileState = _uiWorkProfileState.asStateFlow()

    private val _uiSocialProfileState = MutableStateFlow<ProfileDataModel?>(null)
    val uiSocialProfileState = _uiSocialProfileState.asStateFlow()

    fun generateQRCode(profile: ProfileDataModel): ByteArray? {
        return if (profile.profileType == "Work") {
            qrCodeGenerator.generateWorkQRCode(
                WorkQRCodeModel(
                    fullName = profile.fullName,
                    email = profile.email,
                    phoneNumber = profile.phoneNumber,
                    organization = profile.organization,
                    role = profile.role,
                    socialLinkModel = profile.socialLinks
                )
            )
        } else {
            qrCodeGenerator.generateSocialQRCode(
                SocialQRCodeModel(
                    fullName = profile.fullName,
                    email = profile.email,
                    phoneNumber = profile.phoneNumber,
                    socialLinks = profile.socialLinks
                )
            )
        }
    }

    init {
        fetchProfiles()
    }

    private fun fetchProfiles() {
        viewModelScope.launch {
            profileRepository.getWorkProfile().collect { result ->
                _uiWorkProfileState.value = result.getOrNull()
            }
        }
        viewModelScope.launch {
            profileRepository.getSocialProfile().collect { result ->
                _uiSocialProfileState.value = result.getOrNull()
            }
        }
    }
}
