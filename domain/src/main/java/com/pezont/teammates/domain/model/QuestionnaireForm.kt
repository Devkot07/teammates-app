package com.pezont.teammates.domain.model

data class QuestionnaireForm(
    var header: String,
    var description: String,
    var selectedGame: Games? = null,
) {
    fun isNotEmpty(): Boolean {
        return header.isNotEmpty() && description.isNotEmpty() && selectedGame != null
    }
}