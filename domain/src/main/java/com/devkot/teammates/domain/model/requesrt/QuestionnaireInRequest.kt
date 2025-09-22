package com.devkot.teammates.domain.model.requesrt

data class QuestionnaireInRequest(
    val header: String,
    val game: String,
    val description: String,
    val authorId: String
)
