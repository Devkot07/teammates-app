package com.pezont.teammates.domain.repository

import com.pezont.teammates.domain.model.LoginRequest
import com.pezont.teammates.domain.model.LoginResponse

interface AuthRepository {

    suspend fun login(request: LoginRequest): Result<LoginResponse>

}