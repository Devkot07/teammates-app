package com.pezont.teammates.domain.usecase

import com.pezont.teammates.domain.model.requesrt.LoginRequest
import com.pezont.teammates.domain.repository.AuthRepository
import com.pezont.teammates.domain.repository.UserDataRepository
import java.time.Instant
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userDataRepository: UserDataRepository,
) {
    suspend operator fun invoke(nickname: String, password: String): Result<Unit> =
        runCatching {
            val nowTimeInMillis = Instant.now().toEpochMilli()
            val refreshTokenExpirationTime = nowTimeInMillis + 1000L*60L*60L*24L*100L
            val response = authRepository.login(LoginRequest(nickname, password)).getOrThrow()
            userDataRepository.saveAccessToken(response.accessToken)
            userDataRepository.saveRefreshToken(response.refreshToken)
            userDataRepository.saveUser(response.user)
            userDataRepository.saveRefreshTokenExpirationTime(refreshTokenExpirationTime)
        }
}
