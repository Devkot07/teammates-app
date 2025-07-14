package com.pezont.teammates.data.dto

import com.google.gson.annotations.SerializedName


data class LoginResponseDto(
    @SerializedName("user")
    val user: UserDto,
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String
)
