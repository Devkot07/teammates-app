package com.pezont.teammates.domain.model.response

data class UpdateTokenResponse (
    val accessToken: String,
    val refreshToken: String
)