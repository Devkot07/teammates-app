package com.pezont.teammates.domain.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateTokenRequest (
    @SerializedName("public_id")
    val publicId: String,
    @SerializedName("refresh_token")
    val refreshToken: String
)