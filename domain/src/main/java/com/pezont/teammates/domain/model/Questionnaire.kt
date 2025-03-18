package com.pezont.teammates.domain.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Questionnaire(
    val header: String = "",
    val game: String = "",
    val description: String = "",
    @SerializedName("author_id")
    val authorId: String = "",
    @SerializedName("id")
    val questionnaireId: String = "",
    @SerializedName("image_path")
    val imagePath: String = ""
)
