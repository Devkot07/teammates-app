package com.devkot.teammates.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LikeUserRequestDto (
    @SerializedName("liked_by_id")
    val userId: String,
    @SerializedName("liked_id")
    val likedUserId: String
)