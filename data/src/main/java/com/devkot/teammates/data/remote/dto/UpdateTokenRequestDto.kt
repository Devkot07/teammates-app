package com.devkot.teammates.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UpdateTokenRequestDto (
    @SerializedName("public_id")
    val publicId: String,
    @SerializedName("refresh_token")
    val refreshToken: String
)