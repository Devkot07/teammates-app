package com.devkot.teammates.domain.model.requesrt

data class UpdateTokenRequest (
    val publicId: String,
    val refreshToken: String
)