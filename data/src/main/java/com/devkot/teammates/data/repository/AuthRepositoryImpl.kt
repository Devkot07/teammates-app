package com.devkot.teammates.data.repository

import android.content.Context
import com.devkot.teammates.data.remote.api.TeammatesAuthApiService
import com.devkot.teammates.data.mapper.toDto
import com.devkot.teammates.data.remote.network.NetworkManager
import com.devkot.teammates.domain.model.requesrt.LoginRequest
import com.devkot.teammates.domain.model.LoginResponse
import com.devkot.teammates.domain.model.requesrt.UpdateTokenRequest
import com.devkot.teammates.domain.model.response.UpdateTokenResponse
import com.devkot.teammates.domain.repository.AuthRepository
import java.io.IOException


class AuthRepositoryImpl(
    private val authApiService: TeammatesAuthApiService,
    private val context: Context
) : AuthRepository {

    override suspend fun login(request: LoginRequest): Result<LoginResponse> {

        if (!NetworkManager.isNetworkAvailable(context)) {
            return Result.failure(IOException("No internet connection"))
        }
        return try {
            Result.success(authApiService.login(request.toDto()).toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun updateTokens(request: UpdateTokenRequest): Result<UpdateTokenResponse> {

        if (!NetworkManager.isNetworkAvailable(context)) {
            return Result.failure(IOException("No internet connection"))
        }
        return try {
            Result.success(authApiService.updateTokens(request.toDto()).toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }

    }
}
