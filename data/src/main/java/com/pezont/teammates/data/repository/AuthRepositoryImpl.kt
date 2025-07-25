package com.pezont.teammates.data.repository

import android.content.Context
import com.pezont.teammates.data.api.TeammatesAuthApiService
import com.pezont.teammates.data.mapper.toDto
import com.pezont.teammates.data.network.NetworkManager
import com.pezont.teammates.domain.model.requesrt.LoginRequest
import com.pezont.teammates.domain.model.LoginResponse
import com.pezont.teammates.domain.model.requesrt.UpdateTokenRequest
import com.pezont.teammates.domain.model.response.UpdateTokenResponse
import com.pezont.teammates.domain.repository.AuthRepository
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
