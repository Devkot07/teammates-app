package com.pezont.teammates.data

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.pezont.teammates.domain.model.LoginResponse
import com.pezont.teammates.domain.model.LoginRequest
import com.pezont.teammates.domain.repository.AuthRepository
import java.io.IOException


class AuthRepositoryImpl(
    private val authApiService: TeammatesAuthApiService,
    private val context: Context
) : AuthRepository {

    @SuppressLint("ServiceCast")
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override suspend fun login(nickname: String, password: String): Result<LoginResponse> {
        if (!isNetworkAvailable()) return Result.failure(IOException("No internet connection"))
        return try {
            val request = LoginRequest(nickname, password)
            Result.success(authApiService.login(request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}
