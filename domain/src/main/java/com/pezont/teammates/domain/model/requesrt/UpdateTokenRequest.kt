package com.pezont.teammates.domain.model.requesrt

data class UpdateTokenRequest (
    val publicId: String,
    val refreshToken: String
)