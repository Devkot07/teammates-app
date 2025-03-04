package com.pezont.teammates.models

data class QuestionnaireForm(
    var header: String,
    var description: String,
    var selectedGame: Games? = null,
) {
    fun isNotEmpty(): Boolean {
        return header.isNotEmpty() && description.isNotEmpty() && selectedGame != null
    }
}