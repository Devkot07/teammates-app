package com.pezont.teammates.data

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.pezont.teammates.models.CreateQuestionnaireRequest
import com.pezont.teammates.models.Games
import com.pezont.teammates.models.Questionnaire
import com.pezont.teammates.network.TeammatesQuestionnairesApiService
import okhttp3.MultipartBody
import java.io.IOException
import java.util.UUID


interface QuestionnairesRepository {

    suspend fun getQuestionnairesFromRepo(
        token: String,
        userId: Int,
        page: Int?,
        limit: Int?,
        game: Games?,
        authorId: Int?,
        questionnaireId: UUID?,
    ): Result<List<Questionnaire>>

    suspend fun createQuestionnaire(
        token: String,
        header: String,
        game: Games,
        description: String,
        authorId: Int,
        image: MultipartBody.Part?
    ): Result<Questionnaire>

}


class NetworkQuestionnairesRepository(
    private val teammatesQuestionnairesApiService: TeammatesQuestionnairesApiService,
    private val context: Context
) : QuestionnairesRepository {

    @SuppressLint("ServiceCast")
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override suspend fun getQuestionnairesFromRepo(
        token: String,
        userId: Int,
        page: Int?,
        limit: Int?,
        game: Games?,
        authorId: Int?,
        questionnaireId: UUID?,
    ): Result<List<Questionnaire>> {
        if (!isNetworkAvailable()) return Result.failure(IOException("No internet connection"))
        return try {

            Result.success(
                teammatesQuestionnairesApiService.getQuestionnaires(
                    token = "Bearer $token",
                    gameName = game?.name,
                    userId = userId,
                    page = page,
                    limit = limit,
                    authorId = authorId,
                    questionnaireId = questionnaireId,

                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createQuestionnaire(
        token: String,
        header: String,
        game: Games,
        description: String,
        authorId: Int,
        image: MultipartBody.Part?
    ): Result<Questionnaire> {

        if (!isNetworkAvailable()) return Result.failure(IOException("No internet connection"))

        return try {
            Result.success(
                teammatesQuestionnairesApiService.createQuestionnaire(
                    token = "Bearer $token",
                    userId = authorId,
                    questionnaire = CreateQuestionnaireRequest(
                        header,
                        game.name,
                        description,
                        authorId
                    ),
                    image = image
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }

    }


}