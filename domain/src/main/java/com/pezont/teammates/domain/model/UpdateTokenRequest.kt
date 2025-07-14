package com.pezont.teammates.domain.model

data class UpdateTokenRequest (
    val publicId: String,
    val refreshToken: String
)