package com.victorkirui.meetnote.domain.usecase

import com.victorkirui.meetnote.domain.model.ProfileDataModel
import com.victorkirui.meetnote.domain.model.WorkQRCodeModel
import com.victorkirui.meetnote.domain.repository.MyProfileRepository
import com.victorkirui.meetnote.domain.repository.QRCodeGenerator
import com.victorkirui.meetnote.domain.util.ValidationError
import com.victorkirui.meetnote.domain.util.ValidationException
import com.victorkirui.meetnote.domain.util.ValidationUtils

class SaveMyWorkProfileUseCase(
    private val qrCodeGenerator: QRCodeGenerator,
    private val myProfileRepository: MyProfileRepository
) {
    suspend operator fun invoke(profileInput: ProfileDataModel): Result<Unit>{
        //Running Validation on the inputs to ensure they are valid
        if (!ValidationUtils.isValidEmail(profileInput.email) && !ValidationUtils.isValidPhoneNumber(profileInput.phoneNumber)){
            return Result.failure(ValidationException(ValidationError.BothInvalid))
        }

        if (!ValidationUtils.isValidEmail(profileInput.email)){
            return Result.failure(ValidationException(ValidationError.InvalidEmail))
        }

        if (!ValidationUtils.isValidPhoneNumber(profileInput.phoneNumber)){
            return Result.failure(ValidationException(ValidationError.InvalidPhoneNumber))
        }

        val qrCodeMessage = WorkQRCodeModel(
            fullName = profileInput.fullName,
            email = profileInput.email,
            phoneNumber = profileInput.phoneNumber,
            organization = profileInput.organization,
            role = profileInput.role,
            socialLinkModel = profileInput.socialLinks
        )

        val qrCode = qrCodeGenerator.generateWorkQRCode(qrCodeMessage)

        val workProfileData = profileInput.copy(QRCode = qrCode, profileType = "Work")

        return myProfileRepository.saveProfile(workProfileData)
    }
}