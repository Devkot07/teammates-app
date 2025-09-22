package com.devkot.teammates.data.repository

import android.content.Context
import com.devkot.teammates.data.local.database.TeammatesDatabase
import com.devkot.teammates.data.local.database.toDomain
import com.devkot.teammates.data.local.database.toLikeEntity
import com.devkot.teammates.data.remote.api.TeammatesUsersApiService
import com.devkot.teammates.data.remote.dto.LikeQuestionnaireRequestDto
import com.devkot.teammates.data.remote.dto.LikeUserRequestDto
import com.devkot.teammates.data.mapper.toDto
import com.devkot.teammates.data.remote.network.NetworkManager
import com.devkot.teammates.domain.model.response.LikeQuestionnaireResponse
import com.devkot.teammates.domain.model.response.LikeUserResponse
import com.devkot.teammates.domain.model.requesrt.LoadAuthorRequest
import com.devkot.teammates.domain.model.Questionnaire
import com.devkot.teammates.domain.model.response.UpdateUserProfilePhotoResponse
import com.devkot.teammates.domain.model.requesrt.UpdateUserProfileRequest
import com.devkot.teammates.domain.model.User
import com.devkot.teammates.domain.repository.UsersRepository
import okhttp3.MultipartBody
import java.io.IOException


class UsersRepositoryImpl(
    private val teammatesUsersApiService: TeammatesUsersApiService,
    database: TeammatesDatabase,
    private val context: Context
) : UsersRepository {

    private val questionnaireDao = database.questionnaireDao()

    override suspend fun loadLikedQuestionnaires(
        userId: String,
    ): Result<Pair<List<Questionnaire>, Throwable?>> {

        suspend fun loadFromCache() = questionnaireDao.getLikedQuestionnaires().map { it.toDomain() }

        return try {
            val dtoList = teammatesUsersApiService.loadLikedQuestionnaires(
                userId = userId
            )

            val domainList = dtoList.map { it.toDomain() }

            questionnaireDao.insertLikeQuestionnaires(domainList.map { it.toLikeEntity() })

            Result.success(Pair(domainList, null))

        } catch (e: Exception) {
            val cachedQuestionnaires = try { loadFromCache() } catch (_: Exception) { emptyList() }

            Result.success(Pair(cachedQuestionnaires, e))
        }

    }

    override suspend fun loadLikedUsers(userId: String): Result<List<User>> {

        if (!NetworkManager.isNetworkAvailable(context)) {
            return Result.failure(IOException("No internet connection"))
        }
        return try {
            val dtoList = teammatesUsersApiService.loadLikedUsers(
                userId = userId
            )

            val domainList = dtoList.map { it.toDomain() }
            Result.success(domainList)

        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun likeQuestionnaire(
        userId: String,
        questionnaire: Questionnaire
    ): Result<LikeQuestionnaireResponse> {

        if (!NetworkManager.isNetworkAvailable(context)) {
            return Result.failure(IOException("No internet connection"))
        }
        return try {
            val response = teammatesUsersApiService.likeQuestionnaire(
                userId = userId,
                request = LikeQuestionnaireRequestDto(userId, questionnaire.questionnaireId)
            ).toDomain()

            if (questionnaire.questionnaireId == response.likedQuestionnaireId) {
                questionnaireDao.insertLikeQuestionnaire(questionnaire.toLikeEntity())
            }

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun unlikeQuestionnaire(
        userId: String,
        questionnaire: Questionnaire
    ): Result<LikeQuestionnaireResponse> {

        if (!NetworkManager.isNetworkAvailable(context)) {
            return Result.failure(IOException("No internet connection"))
        }
        return try {

            val response = teammatesUsersApiService.unlikeQuestionnaire(
                userId = userId,
                request = LikeQuestionnaireRequestDto(userId, questionnaire.questionnaireId)
            ).toDomain()

            if (questionnaire.questionnaireId == response.likedQuestionnaireId) {
                questionnaireDao.deleteLikeQuestionnaire(questionnaire.toLikeEntity())
            }

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun likeUser(
        userId: String,
        likedUserId: String
    ): Result<LikeUserResponse> {

        if (!NetworkManager.isNetworkAvailable(context)) {
            return Result.failure(IOException("No internet connection"))
        }
        return try {
            Result.success(
                teammatesUsersApiService.likeUser(
                    userId = userId,
                    request = LikeUserRequestDto(userId, likedUserId)
                ).toDomain()
            )
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun unlikeUser(
        userId: String,
        unlikedUserId: String
    ): Result<LikeUserResponse> {

        if (!NetworkManager.isNetworkAvailable(context)) {
            return Result.failure(IOException("No internet connection"))
        }
        return try {
            Result.success(
                teammatesUsersApiService.unlikeUser(
                    userId = userId,
                    request = LikeUserRequestDto(userId, unlikedUserId)
                ).toDomain()
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loadAuthorProfile(
        userId: String,
        request: LoadAuthorRequest
    ): Result<User> {

        if (!NetworkManager.isNetworkAvailable(context)) {
            return Result.failure(IOException("No internet connection"))
        }
        return try {
            Result.success(
                teammatesUsersApiService.loadAuthorProfile(
                    userId = userId,
                    publicId = request.authorId
                ).toDomain()
            )
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun updateUserProfile(
        userId: String,
        request: UpdateUserProfileRequest
    ): Result<User> {

        if (!NetworkManager.isNetworkAvailable(context)) {
            return Result.failure(IOException("No internet connection"))
        }
        return try {
            Result.success(
                teammatesUsersApiService.updateUserProfile(
                    userId = userId,
                    request = request.toDto()
                ).toDomain()
            )
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun updateUserProfilePhoto(
        userId: String,
        image: MultipartBody.Part
    ): Result<UpdateUserProfilePhotoResponse> {

        if (!NetworkManager.isNetworkAvailable(context)) {
            return Result.failure(IOException("No internet connection"))
        }
        return try {
            Result.success(
                teammatesUsersApiService.updateUserProfilePhoto(
                    userId = userId,
                    image = image
                ).toDomain()
            )
        } catch (e: Exception) {
            Result.failure(e)
        }

    }
}