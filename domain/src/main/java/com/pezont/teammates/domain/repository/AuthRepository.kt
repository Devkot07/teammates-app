package com.pezont.teammates.domain.repository

import com.pezont.teammates.domain.model.requesrt.LoginRequest
import com.pezont.teammates.domain.model.LoginResponse
import com.pezont.teammates.domain.model.requesrt.UpdateTokenRequest
import com.pezont.teammates.domain.model.response.UpdateTokenResponse

interface AuthRepository {

    suspend fun login(request: LoginRequest): Result<LoginResponse>

    suspend fun updateTokens(request: UpdateTokenRequest): Result<UpdateTokenResponse>
}