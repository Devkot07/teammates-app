package com.pezont.teammates.data.api

import com.pezont.teammates.data.dto.LikeQuestionnaireRequestDto
import com.pezont.teammates.data.dto.LikeQuestionnaireResponseDto
import com.pezont.teammates.data.dto.LikeUserRequestDto
import com.pezont.teammates.data.dto.LikeUserResponseDto
import com.pezont.teammates.data.dto.QuestionnaireDto
import com.pezont.teammates.data.dto.UpdateUserProfilePhotoResponseDto
import com.pezont.teammates.data.dto.UpdateUserProfileRequestDto
import com.pezont.teammates.data.dto.UserDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface TeammatesUsersApiService {

    @GET("like/questionnaires")
    suspend fun loadLikedQuestionnaires(
        @Header("accept") accept: String = "application/json",
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
    ): List<QuestionnaireDto>

    @GET("like/users")
    suspend fun loadLikedUsers(
        @Header("accept") accept: String = "application/json",
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
    ): List<UserDto>

    @POST("like/questionnaire")
    suspend fun likeQuestionnaire(
        @Header("accept") accept: String = "application/json",
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
        @Body request: LikeQuestionnaireRequestDto,
    ): LikeQuestionnaireResponseDto

    @HTTP(method = "DELETE", path = "like/questionnaire", hasBody = true)
    suspend fun unlikeQuestionnaire(
        @Header("accept") accept: String = "application/json",
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,

        @Body request: LikeQuestionnaireRequestDto,
    ): LikeQuestionnaireResponseDto

    @POST("like/user")
    suspend fun likeUser(
        @Header("accept") accept: String = "application/json",
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
        @Body request: LikeUserRequestDto,
    ): LikeUserResponseDto

    @HTTP(method = "DELETE", path = "like/user", hasBody = true)
    suspend fun unlikeUser(
        @Header("accept") accept: String = "application/json",
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,

        @Body request: LikeUserRequestDto,
    ): LikeUserResponseDto

    @GET("users/profile")
    suspend fun loadAuthorProfile(
        @Header("accept") accept: String = "application/json",
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
        @Query("public_id") publicId: String?,
    ): UserDto

    @PUT("users/update")
    suspend fun updateUserProfile(
        @Header("accept") accept: String = "application/json",
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
        @Body request: UpdateUserProfileRequestDto
    ): UserDto

    @Multipart
    @PUT("users/update/photo")
    suspend fun updateUserProfilePhoto(
        @Header("accept") accept: String = "application/json",
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
        @Part image: MultipartBody.Part
    ): UpdateUserProfilePhotoResponseDto

}