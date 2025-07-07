package com.pezont.teammates.data.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    val nickname: String? = null,
    @SerializedName("public_id", alternate = ["id"])
    val publicId: String? = null,
    val email: String? = null,
    val description: String? = null,
    @SerializedName("image_path")
    val imagePath: String? = null,
)