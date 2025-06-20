package com.pezont.teammates.domain.model

import com.google.gson.annotations.SerializedName

data class UpdateUserProfilePhotoResponse(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("image_path")
    val imagePath: String
)