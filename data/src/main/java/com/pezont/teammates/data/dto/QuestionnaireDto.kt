package com.pezont.teammates.data.dto

import com.google.gson.annotations.SerializedName
import com.pezont.teammates.domain.model.Questionnaire

data class QuestionnaireDto(
    @SerializedName("header")
    val header: String? = null,
    @SerializedName("game")
    val game: String? = null,
    val description: String? = null,
    @SerializedName("author_id")
    val authorId: String? = null,
    @SerializedName("id")
    val questionnaireId: String? = null,
    @SerializedName("image_path")
    val imagePath: String? = null
) {
    fun toDomain(): Questionnaire = Questionnaire(
        header = this.header.orEmpty(),
        game = this.game.orEmpty(),
        description = this.description.orEmpty(),
        authorId = this.authorId.orEmpty(),
        questionnaireId = this.questionnaireId.orEmpty(),
        imagePath = this.imagePath.orEmpty()
    )
}
