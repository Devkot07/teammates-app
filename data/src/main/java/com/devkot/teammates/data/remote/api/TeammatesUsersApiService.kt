package com.devkot.teammates.data.remote.api

import com.devkot.teammates.data.remote.dto.LikeQuestionnaireRequestDto
import com.devkot.teammates.data.remote.dto.LikeQuestionnaireResponseDto
import com.devkot.teammates.data.remote.dto.LikeUserRequestDto
import com.devkot.teammates.data.remote.dto.LikeUserResponseDto
import com.devkot.teammates.data.remote.dto.QuestionnaireDto
import com.devkot.teammates.data.remote.dto.UpdateUserProfilePhotoResponseDto
import com.devkot.teammates.data.remote.dto.UpdateUserProfileRequestDto
import com.devkot.teammates.data.remote.dto.UserDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface TeammatesUsersApiService {

    @GET("like/questionnaires")
    suspend fun loadLikedQuestionnaires(
        @Query("user_id") userId: String,
    ): List<QuestionnaireDto>

    @GET("like/users")
    suspend fun loadLikedUsers(
        @Query("user_id") userId: String,
    ): List<UserDto>

    @POST("like/questionnaire")
    suspend fun likeQuestionnaire(
        @Query("user_id") userId: String,
        @Body request: LikeQuestionnaireRequestDto,
    ): LikeQuestionnaireResponseDto

    @HTTP(method = "DELETE", path = "like/questionnaire", hasBody = true)
    suspend fun unlikeQuestionnaire(
        @Query("user_id") userId: String,
        @Body request: LikeQuestionnaireRequestDto,
    ): LikeQuestionnaireResponseDto

    @POST("like/user")
    suspend fun likeUser(
        @Query("user_id") userId: String,
        @Body request: LikeUserRequestDto,
    ): LikeUserResponseDto

    @HTTP(method = "DELETE", path = "like/user", hasBody = true)
    suspend fun unlikeUser(
        @Query("user_id") userId: String,

        @Body request: LikeUserRequestDto,
    ): LikeUserResponseDto

    @GET("users/profile")
    suspend fun loadAuthorProfile(
        @Query("user_id") userId: String,
        @Query("public_id") publicId: String?,
    ): UserDto

    @PUT("users/update")
    suspend fun updateUserProfile(
        @Query("user_id") userId: String,
        @Body request: UpdateUserProfileRequestDto
    ): UserDto

    @Multipart
    @PUT("users/update/photo")
    suspend fun updateUserProfilePhoto(
        @Query("user_id") userId: String,
        @Part image: MultipartBody.Part
    ): UpdateUserProfilePhotoResponseDto

}