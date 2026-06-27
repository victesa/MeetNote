package com.victorkirui.meetnote.domain.util

sealed interface ValidationError {
    object InvalidEmail : ValidationError
    object InvalidPhoneNumber : ValidationError
    object BothInvalid : ValidationError
    object InvalidSocialProfile: ValidationError
}

class ValidationException(val errorType: ValidationError): Exception()
