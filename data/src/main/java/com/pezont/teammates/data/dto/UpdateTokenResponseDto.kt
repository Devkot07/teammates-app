package com.pezont.teammates.data.dto

import com.google.gson.annotations.SerializedName
import com.pezont.teammates.domain.model.response.UpdateTokenResponse

data class UpdateTokenResponseDto(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String
) {
    fun toDomain(): UpdateTokenResponse =
        UpdateTokenResponse(
            accessToken = this.accessToken,
            refreshToken = this.refreshToken
        )
}