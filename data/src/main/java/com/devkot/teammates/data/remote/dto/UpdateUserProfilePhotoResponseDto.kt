package com.devkot.teammates.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.devkot.teammates.domain.model.response.UpdateUserProfilePhotoResponse

data class UpdateUserProfilePhotoResponseDto(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("image_path")
    val imagePath: String
) {
    fun toDomain(): UpdateUserProfilePhotoResponse =
        UpdateUserProfilePhotoResponse(
            userId = this.userId,
            imagePath = this.imagePath
        )
}