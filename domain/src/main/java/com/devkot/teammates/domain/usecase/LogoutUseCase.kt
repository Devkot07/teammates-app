package com.devkot.teammates.domain.usecase

import com.devkot.teammates.domain.model.User
import com.devkot.teammates.domain.repository.UserDataRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository

) {
    suspend operator fun invoke(): Result<Unit> = runCatching {
        userDataRepository.saveAccessToken("")
        userDataRepository.saveRefreshToken("")
        userDataRepository.saveUser(User())
        userDataRepository.saveRefreshTokenExpirationTime(0L)
    }
}