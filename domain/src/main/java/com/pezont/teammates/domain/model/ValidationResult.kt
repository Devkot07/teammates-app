package com.pezont.teammates.domain.model

sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Error(val errorCode: ValidationError) : ValidationResult()
}

enum class ValidationError {
    HEADER_TOO_SHORT,
    HEADER_TOO_LONG,
    DESCRIPTION_TOO_LONG,
    GAME_NOT_SELECTED
}