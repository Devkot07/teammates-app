package com.pezont.teammates.domain.repository

import com.pezont.teammates.domain.model.response.LikeQuestionnaireResponse
import com.pezont.teammates.domain.model.response.LikeUserResponse
import com.pezont.teammates.domain.model.requesrt.LoadAuthorRequest
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.response.UpdateUserProfilePhotoResponse
import com.pezont.teammates.domain.model.requesrt.UpdateUserProfileRequest
import com.pezont.teammates.domain.model.User
import okhttp3.MultipartBody

interface UsersRepository {

    suspend fun loadLikedQuestionnaires(
        token: String,
        userId: String,
    ): Result<List<Questionnaire>>

    suspend fun loadLikedUsers(
        token: String,
        userId: String,
    ): Result<List<User>>

    suspend fun likeQuestionnaire(
        token: String,
        userId: String,
        likedQuestionnaireId: String
    ): Result<LikeQuestionnaireResponse>

    suspend fun unlikeQuestionnaire(
        token: String,
        userId: String,
        unlikedQuestionnaireId: String
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
