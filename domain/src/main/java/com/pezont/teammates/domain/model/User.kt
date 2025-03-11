package com.pezont.teammates.domain.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

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
