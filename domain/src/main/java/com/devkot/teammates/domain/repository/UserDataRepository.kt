package com.devkot.teammates.domain.repository

import com.devkot.teammates.domain.model.User


interface UserDataRepository {
    suspend fun user(): User
    suspend fun accessToken(): String
    suspend fun refreshToken(): String

    suspend fun refreshTokenExpirationTime(): Long

    suspend fun saveUser(user: User)
    suspend fun saveAccessToken(newAccessToken: String)
    suspend fun saveRefreshToken(newRefreshToken: String)
    suspend fun updateUserProfile(nickname: String? = null, description: String? = null, imagePath: String? = null)
    suspend fun saveRefreshTokenExpirationTime(refreshTokenExpirationTime: Long)
}