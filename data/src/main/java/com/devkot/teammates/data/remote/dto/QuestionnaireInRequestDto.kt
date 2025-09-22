package com.devkot.teammates.data.remote.dto

import com.google.gson.annotations.SerializedName

data class QuestionnaireInRequestDto(
    @SerializedName("header")
    val header: String,
    @SerializedName("game")
    val game: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("author_id")
    val authorId: String
)
