package com.pezont.teammates.network

import com.pezont.teammates.models.CreateQuestionnaireRequest
import com.pezont.teammates.models.Questionnaire
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import java.util.UUID

interface TeammatesQuestionnairesApiService {


    @GET("questionnaires")
    suspend fun getQuestionnaires(
        @Header("accept") accept: String = "application/json",
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int,
        @Query("page") page: Int?,
        @Query("limit") limit: Int?,
        @Query("game") gameName: String?,
        @Query("author_id") authorId: Int?,
        @Query("questionnaire_id") questionnaireId: UUID?,

        ): List<Questionnaire>


    @Multipart
    @POST("questionnaire")
    suspend fun createQuestionnaire(
        @Header("accept") accept: String = "application/json",
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int,
        @Part("questionnaire_in") questionnaire: CreateQuestionnaireRequest,
        @Part image: MultipartBody.Part? = null
    ): Questionnaire


}