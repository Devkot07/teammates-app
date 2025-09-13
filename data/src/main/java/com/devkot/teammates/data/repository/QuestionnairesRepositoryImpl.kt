package com.devkot.teammates.data.repository

import android.content.Context
import com.devkot.teammates.data.local.database.TeammatesDatabase
import com.devkot.teammates.data.local.database.toDomain
import com.devkot.teammates.data.local.database.toDefaultEntity
import com.devkot.teammates.data.mapper.toDto
import com.devkot.teammates.data.remote.api.TeammatesQuestionnairesApiService
import com.devkot.teammates.data.remote.network.NetworkManager
import com.devkot.teammates.domain.model.Questionnaire
import com.devkot.teammates.domain.model.enums.Games
import com.devkot.teammates.domain.model.requesrt.CreateQuestionnaireRequest
import com.devkot.teammates.domain.repository.QuestionnairesRepository
import okhttp3.MultipartBody
import java.io.IOException


class QuestionnairesRepositoryImpl(
    private val teammatesQuestionnairesApiService: TeammatesQuestionnairesApiService,
    database: TeammatesDatabase,
    private val context: Context
) : QuestionnairesRepository {

    private val questionnaireDao = database.questionnaireDao()

    override suspend fun loadQuestionnaires(
        token: String,
        userId: String,
        page: Int?,
        limit: Int?,
        game: Games?,
        authorId: String?,
        questionnaireId: String?,
    ): Result<Pair<List<Questionnaire>, Throwable?>> {

        suspend fun loadFromCache() = questionnaireDao
            .getFilteredQuestionnaires(
                gameName = game?.name,
                page = page ?: 1,
                limit = limit ?: 20,
                authorId = authorId,
                questionnaireId = questionnaireId
            ).map { it.toDomain() }

        return runCatching {
            val dtoList = teammatesQuestionnairesApiService.getQuestionnaires(
                token = "Bearer $token",
                gameName = game?.name,
                userId = userId,
                page = page,
                limit = limit,
                authorId = authorId,
                questionnaireId = questionnaireId,
            )

            val domainList = dtoList.map { it.toDomain() }

            questionnaireDao.insertQuestionnaires(domainList.map { it.toDefaultEntity() })

            Pair(domainList, null)

        }.recoverCatching { error ->
            Pair(loadFromCache(), error)
        }
    }

    override suspend fun createQuestionnaire(
        token: String,
        header: String,
        game: Games,
        description: String,
        authorId: String,
        image: MultipartBody.Part?
    ): Result<Questionnaire> {

        if (!NetworkManager.isNetworkAvailable(context)) {
            return Result.failure(IOException("No internet connection"))
        }
        return try {
            Result.success(
                teammatesQuestionnairesApiService.createQuestionnaire(
                    token = "Bearer $token",
                    userId = authorId,
                    request = QuestionnaireInRequest(
                        header,
                        game.nameOfGame,
                        description,
                        authorId
                    ).toDto(),
                    image = image
                ).toDomain()
            )
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun updateQuestionnaire(
        token: String,
        header: String,
        game: Games,
        description: String,
        authorId: String,
        questionnaireId: String,
        image: MultipartBody.Part?
    ): Result<Questionnaire> {

        if (!NetworkManager.isNetworkAvailable(context)) {
            return Result.failure(IOException("No internet connection"))
        }
        return try {
            Result.success(
                teammatesQuestionnairesApiService.updateQuestionnaire(
                    token = "Bearer $token",
                    userId = authorId,
                    questionnaireId = questionnaireId,
                    questionnaireIdQuery = questionnaireId,
                    request = QuestionnaireInRequest(
                        header,
                        game.nameOfGame,
                        description,
                        authorId
                    ).toDto(),
                    image = image
                ).toDomain()
            )
        } catch (e: Exception) {
            Result.failure(e)
        }

    }
}