package com.pezont.teammates.domain.model

import com.google.gson.annotations.SerializedName


data class UpdateUserProfileRequest(
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("id")
    val publicId: String,
    val description: String,
)