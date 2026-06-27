package com.victorkirui.meetnote

import com.victorkirui.meetnote.domain.MyProfileRepository
import com.victorkirui.meetnote.domain.QRCodeGenerator
import com.victorkirui.meetnote.domain.usecase.SaveMyWorkProfileUseCase
import com.victorkirui.meetnote.domain.ValidationError
import com.victorkirui.meetnote.domain.ValidationException
import com.victorkirui.meetnote.domain.model.ProfileDataModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


class SaveMyWorkProfileUseCaseTest {

    //Mock My Dependencies
    private val qrCodeGenerator: QRCodeGenerator = mockk()
    private val myProfileRepository: MyProfileRepository = mockk()

    //System under Test
    private lateinit var useCase: SaveMyWorkProfileUseCase

    @Before
    fun setup(){
        useCase = SaveMyWorkProfileUseCase(qrCodeGenerator, myProfileRepository)
    }

    @Test
    fun `invoke given invalid email and invalid phone returns BothInvalid exception`() = runTest {
        // Arrange
        val invalidInput = createDummyInput(email = "invalid-email", phoneNumber = "invalid-phone")

        // Act
        val result = useCase(invalidInput)

        // Assert
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull() as? ValidationException
        assertEquals(ValidationError.BothInvalid, exception?.errorType)
    }

    @Test
    fun `invoke given invalid email but valid phone returns InvalidEmail exception`() = runTest {
        // Arrange
        val invalidInput = createDummyInput(email = "invalid-email", phoneNumber = "+1234567890")

        // Act
        val result = useCase(invalidInput)

        // Assert
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull() as? ValidationException
        assertEquals(ValidationError.InvalidEmail, exception?.errorType)
    }

    @Test
    fun `invoke given valid email but invalid phone returns InvalidPhoneNumber exception`() = runTest {
        // Arrange
        val invalidInput = createDummyInput(email = "test@example.com", phoneNumber = "invalid-phone")

        // Act
        val result = useCase(invalidInput)

        // Assert
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull() as? ValidationException
        assertEquals(ValidationError.InvalidPhoneNumber, exception?.errorType)
    }

    @Test
    fun `invoke given valid input generates QR code and saves profile successfully`() = runTest {
        // Arrange
        val validInput = createDummyInput(email = "test@example.com", phoneNumber = "+1234567890")
        val fakeQrCode = byteArrayOf(1, 2, 3)

        // Mock the QR code generator behavior
        every { qrCodeGenerator.generateWorkQRCode(any()) } returns fakeQrCode

        // Mock the repository save operation to return Success
        coEvery { myProfileRepository.saveProfile(any()) } returns Result.success(Unit)

        // Act
        val result = useCase(validInput)

        // Assert
        assertTrue(result.isSuccess)

        // Verify that the repository was actually called with the correctly mapped data
        coVerify(exactly = 1) {
            myProfileRepository.saveProfile(
                match { profile ->
                    profile.fullName == validInput.fullName &&
                            profile.email == validInput.email &&
                            profile.phoneNumber == validInput.phoneNumber &&
                            profile.QRCode.contentEquals(fakeQrCode)
                }
            )
        }
    }
}

// Helper function to easily create inputs with variations
private fun createDummyInput(email: String, phoneNumber: String): ProfileDataModel {
    return ProfileDataModel(
        fullName = "John Doe",
        email = email,
        phoneNumber = phoneNumber,
        organization = "MeetNote Corp",
        role = "Developer",
        socialLinks = emptyList(),
        profilePicture = "",
        profileType = "Work"
    )
}