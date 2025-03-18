package com.pezont.teammates.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.pezont.teammates.data.TeammatesUsersApiService
import com.pezont.teammates.domain.model.Questionnaire
import com.pezont.teammates.domain.repository.UsersRepository
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


}