package com.devkot.teammates.domain.repository

import com.devkot.teammates.domain.model.response.LikeQuestionnaireResponse
import com.devkot.teammates.domain.model.response.LikeUserResponse
import com.devkot.teammates.domain.model.requesrt.LoadAuthorRequest
import com.devkot.teammates.domain.model.Questionnaire
import com.devkot.teammates.domain.model.response.UpdateUserProfilePhotoResponse
import com.devkot.teammates.domain.model.requesrt.UpdateUserProfileRequest
import com.devkot.teammates.domain.model.User
import okhttp3.MultipartBody

interface UsersRepository {

    suspend fun loadLikedQuestionnaires(
        token: String,
        userId: String,
    ): Result<Pair<List<Questionnaire>, Throwable?>>

    suspend fun loadLikedUsers(
        token: String,
        userId: String,
    ): Result<List<User>>

    suspend fun likeQuestionnaire(
        token: String,
        userId: String,
        questionnaire: Questionnaire
    ): Result<LikeQuestionnaireResponse>

    suspend fun unlikeQuestionnaire(
        token: String,
        userId: String,
        questionnaire: Questionnaire
    ): Result<LikeQuestionnaireResponse>

    suspend fun likeUser(
        token: String,
        userId: String,
        likedUserId: String
    ): Result<LikeUserResponse>

    suspend fun unlikeUser(
        token: String,
        userId: String,
        unlikedUserId: String
    ): Result<LikeUserResponse>

    suspend fun loadAuthorProfile(
        token: String,
        userId: String,
        request: LoadAuthorRequest,
    ): Result<User>

    suspend fun updateUserProfile(
        token: String,
        userId: String,
        request: UpdateUserProfileRequest,
    ): Result<User>

    suspend fun updateUserProfilePhoto(
        token: String,
        userId: String,
        image: MultipartBody.Part
    ): Result<UpdateUserProfilePhotoResponse>

}
