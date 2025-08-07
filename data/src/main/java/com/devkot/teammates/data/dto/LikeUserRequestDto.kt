package com.devkot.teammates.data.dto

import com.google.gson.annotations.SerializedName

data class LikeUserRequestDto (
    @SerializedName("liked_by_id")
    val userId: String,
    @SerializedName("liked_id")
    val likedUserId: String
)