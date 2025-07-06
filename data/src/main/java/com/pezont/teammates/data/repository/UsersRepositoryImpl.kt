package com.pezont.teammates.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.pezont.teammates.data.TeammatesUsersApiService
import com.pezont.teammates.domain.model.LoadAuthorRequest
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.model.UpdateUserProfilePhotoResponse
import com.pezont.teammates.domain.model.UpdateUserProfileRequest
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.domain.repository.UsersRepository
import okhttp3.MultipartBody
import java.io.IOException


class UsersRepositoryImpl(
    private val teammatesUsersApiService: TeammatesUsersApiService,
    private val context: Context
) : UsersRepository {

    @SuppressLint("ServiceCast")
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override suspend fun loadLikedQuestionnaires(
        token: String,
        userId: String,
    ): Result<List<Questionnaire>> {
        if (!isNetworkAvailable()) return Result.failure(IOException("No internet connection"))
        return try {

            Result.success(
                teammatesUsersApiService.loadLikedQuestionnaires(
                    token = "Bearer $token",
                    userId = userId
                )
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
        if (!isNetworkAvailable()) return Result.failure(IOException("No internet connection"))
        return try {
            Log.i("DEBUG", "$request")

            Result.success(
                teammatesUsersApiService.loadAuthorProfile(
                    token = "Bearer $token",
                    userId = userId,
                    publicId = request.authorId
                )
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
        if (!isNetworkAvailable()) return Result.failure(IOException("No internet connection"))
        return try {
            Log.i("DEBUG", "$request")

            Result.success(
                teammatesUsersApiService.updateUserProfile(
                    token = "Bearer $token",
                    userId = userId,
                    request = request
                )
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
        if (!isNetworkAvailable()) return Result.failure(IOException("No internet connection"))
        return try {
            Result.success(
                teammatesUsersApiService.updateUserProfilePhoto(
                    token = "Bearer $token",
                    userId = userId,
                    image = image
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}