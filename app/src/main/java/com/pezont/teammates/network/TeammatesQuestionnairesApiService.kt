package com.pezont.teammates.network

import com.pezont.teammates.models.CreateQuestionnaireRequest
import com.pezont.teammates.models.Questionnaire
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface TeammatesQuestionnairesApiService {


    @GET("questionnaires")
    suspend fun getQuestionnairesByGame(
        @Header("accept") accept: String = "application/json",
        @Header("Authorization") token: String,
        @Query("game") gameName: String?,
        @Query("user_id") userId: Int?,
        @Query("page") page: Int,
        @Query("limit") limit: Int
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