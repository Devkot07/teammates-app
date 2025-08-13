package com.devkot.teammates.data.remote.dto

import com.google.gson.annotations.SerializedName


data class UpdateUserProfileRequestDto(
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("id")
    val publicId: String,
    @SerializedName("description")
    val description: String,
)