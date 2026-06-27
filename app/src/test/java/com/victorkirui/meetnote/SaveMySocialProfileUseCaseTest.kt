package com.victorkirui.meetnote

import com.victorkirui.meetnote.domain.MyProfileRepository
import com.victorkirui.meetnote.domain.QRCodeGenerator
import com.victorkirui.meetnote.domain.usecase.SaveMySocialProfileUseCase
import com.victorkirui.meetnote.domain.ValidationError
import com.victorkirui.meetnote.domain.ValidationException
import com.victorkirui.meetnote.domain.model.ProfileDataModel
import com.victorkirui.meetnote.domain.model.SocialLinkModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SaveMySocialProfileUseCaseTest {
    private val myProfileRepository: MyProfileRepository = mockk()
    private val qrCodeGenerator: QRCodeGenerator = mockk()

    private lateinit var useCase: SaveMySocialProfileUseCase

    @Before
    fun setup(){
        useCase = SaveMySocialProfileUseCase(qrCodeGenerator = qrCodeGenerator, myProfileRepository = myProfileRepository)
    }

    @Test
    fun `invoke given invalid social Profile info returns InvalidSocialProfile`() = runTest {

        //Arrange
        val invalidData = createDummyInput(email = "", phoneNumber = "", socialLinks = emptyList())


        //Act
        val result = useCase(invalidData)

        //Assert
        assertTrue(result.isFailure)

        val exception = result.exceptionOrNull() as? ValidationException
        assertEquals(ValidationError.InvalidSocialProfile, exception?.errorType)
    }

    @Test
    fun `invoke given invalid email social info returns InvalidEmail`() = runTest {

        //Arrange
        val invalidData = createDummyInput(email = "test", phoneNumber = "0712345678", socialLinks = listOf(
            SocialLinkModel(
                platform = "jdjd",
                url = "klsks"
            )))

        //Act
        val result = useCase(invalidData)

        //Assert
        assertTrue(result.isFailure)

        val exception = result.exceptionOrNull() as? ValidationException
        assertEquals(ValidationError.InvalidEmail,
            exception?.errorType
        )
    }

    @Test
    fun `invoke given invalid phone number social info returns InvalidPhoneNumber`() = runTest {

        //Arrange
        val invalidData = createDummyInput(email = "test@example.com", phoneNumber = "55", socialLinks = listOf(
            SocialLinkModel(
                platform = "jdjd",
                url = "klsks"
            )))

        //Act
        val result = useCase(invalidData)

        //Arrange
        assertTrue(result.isFailure)

        val exception = result.exceptionOrNull() as? ValidationException
        assertEquals(ValidationError.InvalidPhoneNumber, exception?.errorType)
    }

    @Test
    fun `invoke given valid input generates QR code and saves profile successfully`() = runTest {

        //Arrange
        val validData = createDummyInput(email = "test@example.com", phoneNumber = "0712345678", socialLinks = listOf(
            SocialLinkModel(
                platform = "jdjd",
                url = "klsks"
            )))
        val qrCodeImage = byteArrayOf(1, 2, 3)

        //Mock the QR code generator behavior
        every { qrCodeGenerator.generateSocialQRCode(any()) } returns qrCodeImage

        // Mock the repository save operation to return Success
        coEvery { myProfileRepository.saveProfile(any()) } returns Result.success(Unit)

        //Act
        val result = useCase(validData)

        //Arrange
        assertTrue(result.isSuccess)

        coVerify(exactly = 1) {
            myProfileRepository.saveProfile(
                match {
                    it.fullName == validData.fullName &&
                            it.QRCode.contentEquals(qrCodeImage) &&
                            it.socialLinks == validData.socialLinks &&
                            it.profilePicture == validData.profilePicture &&
                            it.profileType == validData.profileType &&
                            it.email == validData.email &&
                            it.phoneNumber == validData.phoneNumber
                }
            )
        }
    }
}

private fun createDummyInput(email: String, phoneNumber: String, socialLinks: List<SocialLinkModel>): ProfileDataModel {
    return ProfileDataModel(
        fullName = "John Doe",
        email = email,
        phoneNumber = phoneNumber,
        socialLinks = socialLinks,
        profilePicture = "path/to/pic",
        profileType = "Work"
    )
}