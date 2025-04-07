package com.pezont.teammates.domain.repository

import com.pezont.teammates.domain.model.User
import kotlinx.coroutines.flow.Flow


interface UserDataRepository {
    val user: Flow<User>
    val accessToken: Flow<String>
    val refreshToken: Flow<String>

    suspend fun saveUser(user: User)
    suspend fun saveAccessToken(newAccessToken: String)
    suspend fun saveRefreshToken(newRefreshToken: String)
}