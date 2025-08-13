package com.devkot.teammates.domain.model.requesrt

data class CreateQuestionnaireRequest(
    val header: String,
    val game: String,
    val description: String,
    val authorId: String
)
