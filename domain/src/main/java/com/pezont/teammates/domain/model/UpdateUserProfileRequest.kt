package com.pezont.teammates.domain.model

data class UpdateUserProfileRequest(
    val nickname: String,
    val publicId: String,
    val description: String,
)