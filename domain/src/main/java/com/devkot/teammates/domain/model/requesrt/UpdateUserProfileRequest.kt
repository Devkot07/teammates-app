package com.devkot.teammates.domain.model.requesrt

data class UpdateUserProfileRequest(
    val nickname: String,
    val publicId: String,
    val description: String,
)