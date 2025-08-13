package com.devkot.teammates.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.devkot.teammates.domain.model.response.UpdateTokenResponse

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