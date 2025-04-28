package com.pezont.teammates.domain.model.form

import com.pezont.teammates.domain.model.Games

data class QuestionnaireForm(
    var header: String,
    var description: String,
    var selectedGame: Games? = null,
)