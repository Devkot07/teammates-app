package com.pezont.teammates.dummy

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class LoginDummyRequest(
    val username: String,
    val password: String,
)

data class LoginDummyResponse(
    val accessToken: String,
    val refreshToken: String
)

@Serializable
data class UserDummy(
    @SerializedName("username")
    val nickname: String? = null,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("image")
    val imagePath: String? = null,
)


data class RefreshRequest(
    val refreshToken: String,
)