package com.devkot.teammates.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("password")
    val password: String,
)
