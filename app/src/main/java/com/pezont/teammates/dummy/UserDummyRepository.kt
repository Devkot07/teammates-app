package com.pezont.teammates.dummy

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.io.IOException

interface UserDummyRepository {

    suspend fun login(username: String, password: String): Result<LoginDummyResponse>
    suspend fun getCurrentUser(accessToken: String): Result<UserDummy>
    suspend fun refreshAuthToken(refreshToken: String): Result<LoginDummyResponse>
}

class NetworkUserDummyRepository(
    private val authDummyApiService: AuthDummyApiService,
    private val context: Context
) : UserDummyRepository {

    @SuppressLint("ServiceCast")
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override suspend fun login(username: String, password: String): Result<LoginDummyResponse> {
        if (!isNetworkAvailable()) return Result.failure(IOException("No internet connection"))
        return try {
            val request = LoginDummyRequest(username, password)
            Result.success(authDummyApiService.login(request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(accessToken: String): Result<UserDummy> {
        if (!isNetworkAvailable()) return Result.failure(IOException("No internet connection"))
        return try {
            Result.success(authDummyApiService.getCurrentUser("Bearer $accessToken"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshAuthToken(refreshToken: String): Result<LoginDummyResponse> {
        if (!isNetworkAvailable()) return Result.failure(IOException("No internet connection"))
        return try {
            val request = RefreshRequest(refreshToken)
            Result.success(authDummyApiService.refreshAuthToken(request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
