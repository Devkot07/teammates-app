package com.pezont.teammates.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class LoginAuthRequest(
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("password")
    val password: String,
)
@Serializable
data class LoginAuthResponse(
    @SerializedName("user")
    val user: User,
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String
)



@Serializable
data class CreateQuestionnaireRequest(
    val header: String,
    val game: String,
    val description: String,
    @SerializedName("author_id")
    val authorId: String
)

@Serializable
data class Questionnaire(
    val header: String,
    val game: String,
    val description: String,
    @SerializedName("author_id")
    val authorId: String,
    @SerializedName("id")
    val questionnaireId: String = "",
    @SerializedName("image_path")
    val imagePath: String = "",

    )

@Serializable
data class User(
    val nickname: String? = null,
    @SerializedName("public_id")
    val publicId: String? = null,
    val email: String? = null,
    val description: String? = null,
    @SerializedName("image_path")
    val imagePath: String? = null,
)





