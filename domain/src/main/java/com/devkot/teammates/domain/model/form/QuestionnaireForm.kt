package com.devkot.teammates.domain.model.form

import com.devkot.teammates.domain.model.enums.Games

data class QuestionnaireForm(
    var header: String,
    var description: String,
    var selectedGame: Games? = null,
)