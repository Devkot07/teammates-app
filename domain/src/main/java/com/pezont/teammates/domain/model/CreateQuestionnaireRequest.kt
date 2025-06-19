package com.pezont.teammates.domain.model

import com.google.gson.annotations.SerializedName

data class CreateQuestionnaireRequest(
    val header: String,
    val game: String,
    val description: String,
    @SerializedName("author_id")
    val authorId: String
)
