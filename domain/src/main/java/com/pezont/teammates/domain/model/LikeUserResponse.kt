package com.pezont.teammates.domain.model

data class LikeUserResponse(
    val message: String,
    val userId: String,
    val likedUserId: String
)
