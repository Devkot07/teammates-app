package com.pezont.teammates.data.api

import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.UpdateUserProfilePhotoResponse
import com.pezont.teammates.domain.model.UpdateUserProfileRequest
import com.pezont.teammates.domain.model.User
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface TeammatesUsersApiService {

    @GET("like/questionnaires")
    suspend fun loadLikedQuestionnaires(
        @Header("accept") accept: String = "application/json",
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
    ): List<Questionnaire>

    @GET("users/profile")
    suspend fun loadAuthorProfile(
        @Header("accept") accept: String = "application/json",
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
        @Query("public_id") publicId: String?,
    ): User

    @PUT("users/update")
    suspend fun updateUserProfile(
        @Header("accept") accept: String = "application/json",
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
        @Body request: UpdateUserProfileRequest,
        ): User

    @Multipart
    @PUT("users/update/photo")
    suspend fun updateUserProfilePhoto(
        @Header("accept") accept: String = "application/json",
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
        @Part image: MultipartBody.Part
    ): UpdateUserProfilePhotoResponse

}