package com.devkot.teammates.domain.model.response

data class LikeUserResponse(
    val message: String,
    val userId: String,
    val likedUserId: String
)
