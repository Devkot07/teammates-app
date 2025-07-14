package com.pezont.teammates.data.dto

import com.google.gson.annotations.SerializedName

data class UpdateUserProfilePhotoResponseDto(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("image_path")
    val imagePath: String
)