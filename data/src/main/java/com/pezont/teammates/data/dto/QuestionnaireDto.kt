package com.pezont.teammates.data.dto

import com.google.gson.annotations.SerializedName

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
)
