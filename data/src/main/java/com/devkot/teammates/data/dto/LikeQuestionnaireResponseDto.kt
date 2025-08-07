package com.devkot.teammates.data.dto

import com.google.gson.annotations.SerializedName
import com.devkot.teammates.domain.model.response.LikeQuestionnaireResponse

data class LikeQuestionnaireResponseDto(
    @SerializedName("message")
    val message: String,
    @SerializedName("liker_id")
    val userId: String,
    @SerializedName("questionnaire_id")
    val likedQuestionnaireId: String
) {
    fun toDomain(): LikeQuestionnaireResponse =
        LikeQuestionnaireResponse(
            message = this.message,
            userId = this.userId,
            likedQuestionnaireId = this.likedQuestionnaireId
        )

}