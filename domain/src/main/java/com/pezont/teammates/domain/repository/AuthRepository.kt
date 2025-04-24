package com.pezont.teammates.domain.repository

import com.pezont.teammates.domain.model.LoginRequest
import com.pezont.teammates.domain.model.LoginResponse
import com.pezont.teammates.domain.model.UpdateTokenRequest
import com.pezont.teammates.domain.model.UpdateTokenResponse

interface AuthRepository {

    suspend fun login(request: LoginRequest): Result<LoginResponse>

    suspend fun updateTokens(request: UpdateTokenRequest): Result<UpdateTokenResponse>
}