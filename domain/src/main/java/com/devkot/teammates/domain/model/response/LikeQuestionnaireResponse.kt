package com.devkot.teammates.domain.model.response

data class LikeQuestionnaireResponse(
    val message: String,
    val userId: String,
    val likedQuestionnaireId: String
)