package com.pezont.teammates.domain.repository

import com.pezont.teammates.domain.model.enums.Games
import com.pezont.teammates.domain.model.Questionnaire
import okhttp3.MultipartBody


interface QuestionnairesRepository {

    suspend fun loadQuestionnaires(
        token: String,
        userId: String,
        page: Int?,
        limit: Int?,
        game: Games?,
        authorId: String?,
        questionnaireId: String?,
    ): Result<List<Questionnaire>>

    suspend fun createQuestionnaire(
        token: String,
        header: String,
        game: Games,
        description: String,
        authorId: String,
        image: MultipartBody.Part?
    ): Result<Questionnaire>

}
