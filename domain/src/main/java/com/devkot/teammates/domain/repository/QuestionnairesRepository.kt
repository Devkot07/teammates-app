package com.devkot.teammates.domain.repository

import com.devkot.teammates.domain.model.enums.Games
import com.devkot.teammates.domain.model.Questionnaire
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
    ): Result<Pair<List<Questionnaire>, Throwable?>>

    suspend fun createQuestionnaire(
        token: String,
        header: String,
        game: Games,
        description: String,
        authorId: String,
        image: MultipartBody.Part?
    ): Result<Questionnaire>


    suspend fun updateQuestionnaire(
        token: String,
        header: String,
        game: Games,
        description: String,
        authorId: String,
        questionnaireId: String,
        image: MultipartBody.Part?
    ): Result<Questionnaire>

//    suspend fun deleteQuestionnaires(
//        token: String,
//        userId: String,
//        questionnaireId: String?,
//    ): Result<String>

}
