package com.devkot.teammates.data.dto

import com.google.gson.annotations.SerializedName

data class LikeQuestionnaireRequestDto(
    @SerializedName("liker_id")
    val userId: String,
    @SerializedName("questionnaire_id")
    val likedQuestionnaireId: String
)