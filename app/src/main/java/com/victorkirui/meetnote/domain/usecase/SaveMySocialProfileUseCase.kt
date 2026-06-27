package com.victorkirui.meetnote.domain.usecase

import com.victorkirui.meetnote.domain.model.ProfileDataModel
import com.victorkirui.meetnote.domain.model.SocialQRCodeModel
import com.victorkirui.meetnote.domain.repository.MyProfileRepository
import com.victorkirui.meetnote.domain.repository.QRCodeGenerator
import com.victorkirui.meetnote.domain.util.ValidationError
import com.victorkirui.meetnote.domain.util.ValidationException
import com.victorkirui.meetnote.domain.util.ValidationUtils

class SaveMySocialProfileUseCase(private val myProfileRepository: MyProfileRepository,
                                 private val qrCodeGenerator: QRCodeGenerator
) {

    suspend operator fun invoke(
        profileInput: ProfileDataModel
    ): Result<Unit>{

        if(!ValidationUtils.isSocialProfileValid(
                socialLinks = profileInput.socialLinks, phoneNumber = profileInput.phoneNumber,
            email = profileInput.email)){
            return Result.failure(ValidationException(ValidationError.InvalidSocialProfile))
        }

        if (profileInput.email?.isNotEmpty() == true && !ValidationUtils.isValidEmail(email = profileInput.email)){
            return Result.failure(ValidationException(ValidationError.InvalidEmail))
        }

        if(profileInput.phoneNumber?.isNotEmpty() == true && !ValidationUtils.isValidPhoneNumber(
                phone = profileInput.phoneNumber)){
            return Result.failure(ValidationException(ValidationError.InvalidPhoneNumber))
        }

        val qrCodeData = SocialQRCodeModel(
            fullName = profileInput.fullName,
            phoneNumber = profileInput.phoneNumber,
            email = profileInput.email,
            socialLinks = profileInput.socialLinks
        )

        val qrCode = qrCodeGenerator.generateSocialQRCode(qrCodeData)



        val myProfile = profileInput.copy(QRCode = qrCode, profileType = "Social")


        return myProfileRepository.saveProfile(myProfile)
    }
}