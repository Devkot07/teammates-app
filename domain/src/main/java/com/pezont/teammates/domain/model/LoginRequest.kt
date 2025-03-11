package com.pezont.teammates.domain.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("password")
    val password: String,
)
