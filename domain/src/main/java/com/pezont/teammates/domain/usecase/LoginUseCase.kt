package com.pezont.teammates.domain.usecase

import com.pezont.teammates.domain.repository.AuthRepository
import com.pezont.teammates.domain.repository.UserDataRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(nickname: String, password: String): Result<Unit> =
        runCatching {
            val response = authRepository.login(nickname, password).getOrThrow()
            userDataRepository.saveAccessToken(response.accessToken)
            userDataRepository.saveRefreshToken(response.refreshToken)
            userDataRepository.saveUser(response.user)
        }
}
