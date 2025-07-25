package com.pezont.teammates.data.dto

import com.google.gson.annotations.SerializedName
import com.pezont.teammates.domain.model.response.LikeUserResponse

data class LikeUserResponseDto(
    @SerializedName("message")
    val message: String,
    @SerializedName("liker_id")
    val userId: String,
    @SerializedName("liked_user_id")
    val likedUserId: String
) {
    fun toDomain(): LikeUserResponse =
        LikeUserResponse(
            message = this.message,
            userId = this.userId,
            likedUserId = this.likedUserId
        )

}