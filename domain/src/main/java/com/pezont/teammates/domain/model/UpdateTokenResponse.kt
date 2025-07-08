package com.pezont.teammates.domain.model

data class UpdateTokenResponse (
    val accessToken: String,
    val refreshToken: String
)