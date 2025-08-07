package com.devkot.teammates.domain.model.response

data class UpdateTokenResponse (
    val accessToken: String,
    val refreshToken: String
)