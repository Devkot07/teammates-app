package com.pezont.teammates.data.dto

import com.google.gson.annotations.SerializedName
import com.pezont.teammates.domain.model.LoginResponse

data class LoginResponseDto(
    @SerializedName("user")
    val user: UserDto,
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String
) {
    fun toDomain(): LoginResponse =
        LoginResponse(
            user = this.user.toDomain(),
            accessToken = this.accessToken,
            refreshToken = this.refreshToken
        )
}
