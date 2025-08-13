package com.devkot.teammates.domain.repository

import com.devkot.teammates.domain.model.requesrt.LoginRequest
import com.devkot.teammates.domain.model.LoginResponse
import com.devkot.teammates.domain.model.requesrt.UpdateTokenRequest
import com.devkot.teammates.domain.model.response.UpdateTokenResponse

interface AuthRepository {

    suspend fun login(request: LoginRequest): Result<LoginResponse>

    suspend fun updateTokens(request: UpdateTokenRequest): Result<UpdateTokenResponse>
}