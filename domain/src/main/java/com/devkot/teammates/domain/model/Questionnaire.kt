package com.devkot.teammates.domain.model

data class Questionnaire(
    val header: String,
    val game: String,
    val description: String,
    val authorId: String,
    val questionnaireId: String,
    val imagePath: String,
) {
    constructor() : this("", "", "", "", "", "")
}
