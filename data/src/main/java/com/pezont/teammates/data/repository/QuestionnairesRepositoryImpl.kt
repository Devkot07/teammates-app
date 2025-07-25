package com.pezont.teammates.data.repository

import android.content.Context
import com.pezont.teammates.data.api.TeammatesQuestionnairesApiService
import com.pezont.teammates.data.mapper.toDto
import com.pezont.teammates.data.network.NetworkManager
import com.pezont.teammates.domain.model.requesrt.CreateQuestionnaireRequest
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.enums.Games
import com.pezont.teammates.domain.repository.QuestionnairesRepository
import okhttp3.MultipartBody
import java.io.IOException


class QuestionnairesRepositoryImpl(
    private val teammatesQuestionnairesApiService: TeammatesQuestionnairesApiService,
    private val context: Context
) : QuestionnairesRepository {

    override suspend fun loadQuestionnaires(
        token: String,
        userId: String,
        page: Int?,
        limit: Int?,
        game: Games?,
        authorId: String?,
        questionnaireId: String?,
    ): Result<List<Questionnaire>> {

        if (!NetworkManager.isNetworkAvailable(context)) {
            return Result.failure(IOException("No internet connection"))
        }
        return try {
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
            Result.success(domainList)

        } catch (e: Exception) {
            Result.failure(e)
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
                    request = CreateQuestionnaireRequest(
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