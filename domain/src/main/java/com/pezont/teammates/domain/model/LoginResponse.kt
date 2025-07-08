package com.pezont.teammates.domain.model

data class LoginResponse(
    val user: User,
    val accessToken: String,
    val refreshToken: String
)
