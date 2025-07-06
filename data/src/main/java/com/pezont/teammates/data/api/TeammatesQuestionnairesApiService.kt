package com.pezont.teammates.data.api

import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.CreateQuestionnaireRequest
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface TeammatesQuestionnairesApiService {


    @GET("questionnaires")
    suspend fun getQuestionnaires(
        @Header("accept") accept: String = "application/json",
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
        @Query("page") page: Int?,
        @Query("limit") limit: Int?,
        @Query("game") gameName: String?,
        @Query("author_id") authorId: String?,
        @Query("questionnaire_id") questionnaireId: String?,

        ): List<Questionnaire>


    @Multipart
    @POST("questionnaire")
    suspend fun createQuestionnaire(
        @Header("accept") accept: String = "application/json",
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
        @Part("questionnaire_in") questionnaire: CreateQuestionnaireRequest,
        @Part image: MultipartBody.Part? = null
    ): Questionnaire


}