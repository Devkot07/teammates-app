package com.pezont.teammates.data.repository

import android.content.Context
import com.pezont.teammates.data.api.TeammatesUsersApiService
import com.pezont.teammates.data.dto.LikeQuestionnaireRequestDto
import com.pezont.teammates.data.dto.LikeUserRequestDto
import com.pezont.teammates.data.mapper.toDto
import com.pezont.teammates.data.network.NetworkManager
import com.pezont.teammates.domain.model.response.LikeQuestionnaireResponse
import com.pezont.teammates.domain.model.response.LikeUserResponse
import com.pezont.teammates.domain.model.requesrt.LoadAuthorRequest
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.response.UpdateUserProfilePhotoResponse
import com.pezont.teammates.domain.model.requesrt.UpdateUserProfileRequest
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.domain.repository.UsersRepository
import okhttp3.MultipartBody
import java.io.IOException


class UsersRepositoryImpl(
    private val teammatesUsersApiService: TeammatesUsersApiService,
    private val context: Context
) : UsersRepository {

    override suspend fun loadLikedQuestionnaires(
        token: String,
        userId: String,
    ): Result<List<Questionnaire>> {

        if (!NetworkManager.isNetworkAvailable(context)) {
            return Result.failure(IOException("No internet connection"))
        }
        return try {
            val dtoList = teammatesUsersApiService.loadLikedQuestionnaires(
                token = "Bearer $token",
                userId = userId
            )

            val domainList = dtoList.map { it.toDomain() }
            Result.success(domainList)

        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun loadLikedUsers(token: String, userId: String): Result<List<User>> {

        if (!NetworkManager.isNetworkAvailable(context)) {
            return Result.failure(IOException("No internet connection"))
        }
        return try {
            val dtoList = teammatesUsersApiService.loadLikedUsers(
                token = "Bearer $token",
                userId = userId
            )

            val domainList = dtoList.map { it.toDomain() }
            Result.success(domainList)

        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun likeQuestionnaire(
        token: String,
        userId: String,
        likedQuestionnaireId: String
    ): Result<LikeQuestionnaireResponse> {

        if (!NetworkManager.isNetworkAvailable(context)) {
            return Result.failure(IOException("No internet connection"))
        }
        return try {
            Result.success(
                teammatesUsersApiService.likeQuestionnaire(
                    token = "Bearer $token",
                    userId = userId,
                    request = LikeQuestionnaireRequestDto(userId, likedQuestionnaireId)
                ).toDomain()
            )
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun unlikeQuestionnaire(
        token: String,
        userId: String,
        unlikedQuestionnaireId: String
    ): Result<LikeQuestionnaireResponse> {

        if (!NetworkManager.isNetworkAvailable(context)) {
            return Result.failure(IOException("No internet connection"))
        }
        return try {
            Result.success(
                teammatesUsersApiService.unlikeQuestionnaire(
                    token = "Bearer $token",
                    userId = userId,
                    request = LikeQuestionnaireRequestDto(userId, unlikedQuestionnaireId)
                ).toDomain()
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun likeUser(
        token: String,
        userId: String,
        likedUserId: String
    ): Result<LikeUserResponse> {

        if (!NetworkManager.isNetworkAvailable(context)) {
            return Result.failure(IOException("No internet connection"))
        }
        return try {
            Result.success(
                teammatesUsersApiService.likeUser(
                    token = "Bearer $token",
                    userId = userId,
                    request = LikeUserRequestDto(userId, likedUserId)
                ).toDomain()
            )
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun unlikeUser(
        token: String,
        userId: String,
        unlikedUserId: String
    ): Result<LikeUserResponse> {

        if (!NetworkManager.isNetworkAvailable(context)) {
            return Result.failure(IOException("No internet connection"))
        }
        return try {
            Result.success(
                teammatesUsersApiService.unlikeUser(
                    token = "Bearer $token",
                    userId = userId,
                    request = LikeUserRequestDto(userId, unlikedUserId)
                ).toDomain()
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loadAuthorProfile(
        token: String,
        userId: String,
        request: LoadAuthorRequest
    ): Result<User> {

        if (!NetworkManager.isNetworkAvailable(context)) {
            return Result.failure(IOException("No internet connection"))
        }
        return try {
            Result.success(
                teammatesUsersApiService.loadAuthorProfile(
                    token = "Bearer $token",
                    userId = userId,
                    publicId = request.authorId
                ).toDomain()
            )
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun updateUserProfile(
        token: String,
        userId: String,
        request: UpdateUserProfileRequest
    ): Result<User> {

        if (!NetworkManager.isNetworkAvailable(context)) {
            return Result.failure(IOException("No internet connection"))
        }
        return try {
            Result.success(
                teammatesUsersApiService.updateUserProfile(
                    token = "Bearer $token",
                    userId = userId,
                    request = request.toDto()
                ).toDomain()
            )
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun updateUserProfilePhoto(
        token: String,
        userId: String,
        image: MultipartBody.Part
    ): Result<UpdateUserProfilePhotoResponse> {

        if (!NetworkManager.isNetworkAvailable(context)) {
            return Result.failure(IOException("No internet connection"))
        }
        return try {
            Result.success(
                teammatesUsersApiService.updateUserProfilePhoto(
                    token = "Bearer $token",
                    userId = userId,
                    image = image
                ).toDomain()
            )
        } catch (e: Exception) {
            Result.failure(e)
        }

    }
}