package com.pezont.teammates.domain.model

data class LikeQuestionnaireResponse(
    val message: String,
    val userId: String,
    val likedQuestionnaireId: String
)