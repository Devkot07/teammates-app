package com.devkot.teammates.data.remote.api

import com.devkot.teammates.data.remote.dto.QuestionnaireDto
import com.devkot.teammates.data.remote.dto.QuestionnaireInRequestDto
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
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
        @Query("questionnaire_id") questionnaireId: String?
    ): List<QuestionnaireDto>

    @Multipart
    @POST("questionnaire")
    suspend fun  createQuestionnaire(
        @Header("accept") accept: String = "application/json",
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
        @Part("questionnaire_in") request: QuestionnaireInRequestDto,
        @Part image: MultipartBody.Part? = null
    ): QuestionnaireDto

    @Multipart
    @PUT("questionnaire/{questionnaire_id}")
    suspend fun updateQuestionnaire(
        @Header("accept") accept: String = "application/json",
        @Header("Authorization") token: String,
        @Path("questionnaire_id") questionnaireId: String,
        @Query("user_id") userId: String,
        @Part("questionnaire_in") request: QuestionnaireInRequestDto,
        @Part image: MultipartBody.Part? = null
    ): QuestionnaireDto

    @DELETE("questionnaire/{questionnaire_id}")
    suspend fun deleteQuestionnaire(
        @Header("accept") accept: String = "application/json",
        @Header("Authorization") token: String,
        @Path("questionnaire_id") questionnaireId: String,
        @Query("user_id") userId: String,
    )

}