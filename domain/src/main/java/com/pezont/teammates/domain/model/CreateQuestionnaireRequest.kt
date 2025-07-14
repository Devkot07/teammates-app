package com.pezont.teammates.domain.model

data class CreateQuestionnaireRequest(
    val header: String,
    val game: String,
    val description: String,
    val authorId: String
)
