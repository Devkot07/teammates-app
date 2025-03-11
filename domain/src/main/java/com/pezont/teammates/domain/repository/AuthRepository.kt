package com.pezont.teammates.domain.repository

import com.pezont.teammates.domain.model.LoginResponse

interface AuthRepository {

    suspend fun login(nickname: String, password: String): Result<LoginResponse>

}