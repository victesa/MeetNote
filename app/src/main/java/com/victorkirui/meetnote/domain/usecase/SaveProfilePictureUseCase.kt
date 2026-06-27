package com.victorkirui.meetnote.domain.usecase

import com.victorkirui.meetnote.domain.repository.MyProfileRepository

class SaveProfilePictureUseCase(
    private val profileRepository: MyProfileRepository
) {
    suspend operator fun invoke(temporaryUriString: String): Result<String>{
        return profileRepository.saveImageToInternalStorage(temporaryUriString)
    }
}